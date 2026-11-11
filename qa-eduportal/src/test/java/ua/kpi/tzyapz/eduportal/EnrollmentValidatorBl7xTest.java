package ua.kpi.tzyapz.eduportal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * BL-72, BL-74, BL-76, BL-78 — автор: Білогривий (АО), задача 5.4.
 * Курс: ключ "Tkey-2026", період 2026-11-01…2026-11-30, місткість 30.
 * Очікування — з automation-plan.md, розд. 3 (SRS 1.1).
 *
 * Окремий файл і вкладені класи, щоб не конфліктувати з EnrollmentValidatorTest Мележика (5.5).
 * Сигнатури звірено з кодом v1: методи екземплярні (new EnrollmentValidator()), Course і Student — record-и.
 * Червоні тести — дефекти модуля v1 (див. audit.md), @Disabled не використовується.
 */
@Tag("unit")
class EnrollmentValidatorBl7xTest {

    private static final EnrollmentValidator VALIDATOR = new EnrollmentValidator();
    private static final String KEY = "Tkey-2026";
    private static final LocalDate START = LocalDate.of(2026, 11, 1);
    private static final LocalDate END = LocalDate.of(2026, 11, 30);

    @Nested
    @DisplayName("BL-72 keyMatches (точний збіг, з урахуванням регістру)")
    class KeyMatches {

        static Stream<Arguments> cases() {
            return Stream.of(
                Arguments.of("Tkey-2026", true),
                Arguments.of("tkey-2026", false),   // регістр, TC-007
                Arguments.of("Tkey-2026 ", false),  // пробіл у кінці: інтерпретація, див. Q3
                Arguments.of("", false)
            );
        }

        @ParameterizedTest(name = "[{index}] введено \"{0}\" → {1}")
        @MethodSource("cases")
        @DisplayName("BL-72: порівняння введеного кодового слова")
        void enteredKey(String entered, boolean expected) {
            assertEquals(expected, VALIDATOR.keyMatches(courseWithKey(KEY), entered));
        }

        @org.junit.jupiter.api.Test
        @DisplayName("BL-72: ключ курсу не заданий (null) → false, SRS 1.1 С-11 (дефект #45)")
        void courseKeyNotSet() {
            assertEquals(false, VALIDATOR.keyMatches(courseWithKey(null), KEY));
        }
    }

    @Nested
    @DisplayName("BL-74 withinPeriod (межі включно)")
    class WithinPeriod {

        @ParameterizedTest(name = "[{index}] today={0} → {1}")
        @CsvSource({
            "2026-10-31, false",  // день до початку
            "2026-11-01, true",   // початок
            "2026-11-15, true",   // середина
            "2026-11-30, true",   // кінець
            "2026-12-01, false"   // день після кінця
        })
        @DisplayName("BL-74: дата в періоді 2026-11-01…2026-11-30; рядок 2026-12-01 червоний через дефект #35")
        void period(LocalDate today, boolean expected) {
            assertEquals(expected, VALIDATOR.withinPeriod(courseWithPeriod(START, END), today));
        }
    }

    @Nested
    @DisplayName("BL-76 hasCapacity (місткість 30)")
    class HasCapacity {

        @ParameterizedTest(name = "[{index}] {0} із {1} → {2}")
        @CsvSource({
            "29, 30, true",
            "30, 30, false",
            "31, 30, false"
        })
        @DisplayName("BL-76: вільне місце є лише коли enrolled < capacity; рядок 30 із 30 червоний через дефект #36")
        void capacity(int enrolled, int capacity, boolean expected) {
            assertEquals(expected, VALIDATOR.hasCapacity(courseWithSeats(enrolled, capacity)));
        }

        @ParameterizedTest(name = "[{index}] enrolled={0}, capacity={1} → IAE")
        @CsvSource({
            "10, 0",    // capacity 0
            "10, -1",   // capacity < 0
            "-1, 30"    // enrolled < 0
        })
        @DisplayName("BL-76: некоректні числа → IllegalArgumentException (дефект #46)")
        void invalid(int enrolled, int capacity) {
            assertThrows(IllegalArgumentException.class, () -> VALIDATOR.hasCapacity(courseWithSeats(enrolled, capacity)));
        }
    }

    @Nested
    @DisplayName("BL-78 prerequisiteMet (поріг 60.0 включно)")
    class PrerequisiteMet {

        static Stream<Arguments> cases() {
            return Stream.of(
                Arguments.of("передумови немає", null, Map.<String, Double>of(), true),
                Arguments.of("BIO-101 = 60.0 (межа)", "BIO-101", Map.of("BIO-101", 60.0), true),
                Arguments.of("BIO-101 = 59.99", "BIO-101", Map.of("BIO-101", 59.99), false),
                Arguments.of("BIO-101 = 100.0", "BIO-101", Map.of("BIO-101", 100.0), true),
                Arguments.of("курсу немає в completedCourses", "BIO-101", Map.<String, Double>of(), false),
                Arguments.of("є лише інший курс", "BIO-101", Map.of("MAT-100", 90.0), false)
            );
        }

        @ParameterizedTest(name = "[{index}] {0} → {3}")
        @MethodSource("cases")
        @DisplayName("BL-78: передумовний курс складено з балом ≥ 60.0")
        void prerequisite(String title, String prerequisite, Map<String, Double> completed, boolean expected) {
            assertEquals(expected, VALIDATOR.prerequisiteMet(studentWith(completed), courseWithPrerequisite(prerequisite)), title);
        }
    }

    // ---- побудова даних (Course і Student — record-и v1) ----

    private static Course courseWithKey(String key) {
        return new Course("C1", key, START, END, 30, 10, null);
    }

    private static Course courseWithPeriod(LocalDate start, LocalDate end) {
        return new Course("C1", KEY, start, end, 30, 10, null);
    }

    private static Course courseWithSeats(int enrolled, int capacity) {
        return new Course("C1", KEY, START, END, capacity, enrolled, null);
    }

    private static Course courseWithPrerequisite(String prerequisiteId) {
        return new Course("C1", KEY, START, END, 30, 10, prerequisiteId);
    }

    private static Student studentWith(Map<String, Double> completedCourses) {
        return new Student("S1", completedCourses);
    }
}
