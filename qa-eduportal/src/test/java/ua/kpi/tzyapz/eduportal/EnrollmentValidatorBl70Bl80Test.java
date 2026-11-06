package ua.kpi.tzyapz.eduportal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * BL-70 canEnroll і BL-80 violations (автор: Мележик, КТ).
 * Очікувані значення взято з SRS 1.1, а не з поточної поведінки коду.
 * Тексти порушень — із SRS 1.1 (BL-80). Розбіжність із кодами KEY/PERIOD/... у коді v1
 * винесено як питання замовнику Q1 (Issue #38). Дефекти: #35 (BL-74), #36 (BL-76).
 * Тест, що впав, не вимикається (@Disabled заборонено): він посилається на Issue.
 */
@Tag("unit")
@DisplayName("EnrollmentValidator: BL-70 canEnroll, BL-80 violations")
class EnrollmentValidatorBl70Bl80Test {

    private static final String MSG_KEY = "Невірний код зарахування";
    private static final String MSG_PERIOD = "Поза періодом зарахування";
    private static final String MSG_CAPACITY = "Немає вільних місць";
    private static final String MSG_PREREQ = "Не складено передумовний курс";

    private static final String KEY = "Tkey-2026";
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 15);
    private static final LocalDate START = LocalDate.of(2026, 11, 1);
    private static final LocalDate END = LocalDate.of(2026, 11, 30);

    private final EnrollmentValidator validator = new EnrollmentValidator();

    private static Student student(Map<String, Double> done) {
        return new Student("S-1", done);
    }

    /** Базовий «чистий» курс: 10 із 30 місць, без передумови. */
    private static Course course(int enrolled, String prerequisite) {
        return new Course("C-1", KEY, START, END, 30, enrolled, prerequisite);
    }

    @Nested
    @DisplayName("BL-70 canEnroll")
    class CanEnroll {

        @Test
        @DisplayName("BL-70: усі умови виконано → true")
        void allConditionsMet() {
            assertTrue(validator.canEnroll(student(Map.of()), course(10, null), TODAY, KEY));
        }

        @Test
        @DisplayName("BL-70: неправильне кодове слово → false")
        void wrongKey() {
            assertFalse(validator.canEnroll(student(Map.of()), course(10, null), TODAY, "wrong"));
        }

        @Test
        @DisplayName("BL-70: дата після кінця періоду (2026-12-01) → false (Issue #35, BL-74)")
        void afterPeriod() {
            assertFalse(validator.canEnroll(student(Map.of()), course(10, null),
                    LocalDate.of(2026, 12, 1), KEY));
        }

        @Test
        @DisplayName("BL-70: місць немає (30 із 30) → false (Issue #36, BL-76)")
        void noCapacity() {
            assertFalse(validator.canEnroll(student(Map.of()), course(30, null), TODAY, KEY));
        }

        @Test
        @DisplayName("BL-70: передумова BIO-101 не складена → false")
        void prerequisiteMissing() {
            assertFalse(validator.canEnroll(student(Map.of()), course(10, "BIO-101"), TODAY, KEY));
        }

        @Test
        @DisplayName("BL-70: порушено дві умови (ключ і місця) → false")
        void twoViolations() {
            assertFalse(validator.canEnroll(student(Map.of()), course(30, null), TODAY, "wrong"));
        }

        @Test
        @DisplayName("BL-70: межа — останній день періоду (2026-11-30) → true")
        void lastDayOfPeriod() {
            assertTrue(validator.canEnroll(student(Map.of()), course(10, null), END, KEY));
        }

        @Test
        @DisplayName("BL-70: межа — 29 із 30 місць і передумова рівно 60.0 → true")
        void boundaryCapacityAndPrerequisite() {
            assertTrue(validator.canEnroll(student(Map.of("BIO-101", 60.0)),
                    course(29, "BIO-101"), TODAY, KEY));
        }

        @Test
        @DisplayName("BL-70: null-аргумент → IllegalArgumentException")
        void nullArgument() {
            assertThrows(IllegalArgumentException.class,
                    () -> validator.canEnroll(null, course(10, null), TODAY, KEY));
        }
    }

    @Nested
    @DisplayName("BL-80 violations")
    class Violations {

        @Test
        @DisplayName("BL-80: порушень немає → порожній список")
        void none() {
            assertEquals(List.of(),
                    validator.violations(student(Map.of()), course(10, null), TODAY, KEY));
        }

        @Test
        @DisplayName("BL-80: лише ключ (Issue #38 — формат результату)")
        void onlyKey() {
            assertEquals(List.of(MSG_KEY),
                    validator.violations(student(Map.of()), course(10, null), TODAY, "wrong"));
        }

        @Test
        @DisplayName("BL-80: лише період (Issues #38, #35)")
        void onlyPeriod() {
            assertEquals(List.of(MSG_PERIOD),
                    validator.violations(student(Map.of()), course(10, null),
                            LocalDate.of(2026, 12, 1), KEY));
        }

        @Test
        @DisplayName("BL-80: лише місця (Issues #38, #36)")
        void onlyCapacity() {
            assertEquals(List.of(MSG_CAPACITY),
                    validator.violations(student(Map.of()), course(30, null), TODAY, KEY));
        }

        @Test
        @DisplayName("BL-80: лише передумова (Issue #38 — формат результату)")
        void onlyPrerequisite() {
            assertEquals(List.of(MSG_PREREQ),
                    validator.violations(student(Map.of()), course(10, "BIO-101"), TODAY, KEY));
        }

        @Test
        @DisplayName("BL-80: усі чотири порушення у порядку умов BL-70 (Issues #38, #35, #36)")
        void allFourInOrder() {
            assertEquals(List.of(MSG_KEY, MSG_PERIOD, MSG_CAPACITY, MSG_PREREQ),
                    validator.violations(student(Map.of()), course(30, "BIO-101"),
                            LocalDate.of(2026, 12, 1), "wrong"));
        }

        @Test
        @DisplayName("BL-80: ключ і місця — порядок «ключ, місця» (Issues #38, #36)")
        void keyAndCapacityOrder() {
            assertEquals(List.of(MSG_KEY, MSG_CAPACITY),
                    validator.violations(student(Map.of()), course(30, null), TODAY, "wrong"));
        }
    }

    /** Усі 16 комбінацій порушених умов. */
    static Stream<Arguments> combinations() {
        List<Arguments> rows = new ArrayList<>();
        for (int mask = 0; mask < 16; mask++) {
            rows.add(Arguments.of(
                    (mask & 1) != 0, (mask & 2) != 0, (mask & 4) != 0, (mask & 8) != 0));
        }
        return rows.stream();
    }

    @ParameterizedTest(name = "ключ={0}, період={1}, місця={2}, передумова={3}")
    @MethodSource("combinations")
    @DisplayName("BL-70/BL-80: canEnroll ⇔ violations порожній; кількість порушень збігається (Issues #35, #36)")
    void canEnrollMatchesViolations(boolean badKey, boolean badPeriod, boolean noSeats,
                                    boolean noPrereq) {
        String key = badKey ? "wrong" : KEY;
        LocalDate today = badPeriod ? LocalDate.of(2026, 12, 1) : TODAY;
        int enrolled = noSeats ? 30 : 10;
        String prereq = noPrereq ? "BIO-101" : null;

        int expected = (badKey ? 1 : 0) + (badPeriod ? 1 : 0) + (noSeats ? 1 : 0) + (noPrereq ? 1 : 0);
        Student s = student(Map.of());
        Course c = course(enrolled, prereq);

        assertEquals(expected, validator.violations(s, c, today, key).size());
        assertEquals(expected == 0, validator.canEnroll(s, c, today, key));
    }
}
