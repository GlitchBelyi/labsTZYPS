package ua.kpi.tzyapz.eduportal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Зразок юніт-тестів для модуля «ЕдуПортал». Показує стиль: @BeforeEach, @DisplayName,
 * @ParameterizedTest, assertEquals/assertTrue/assertThrows. Ваші тести спринта 4 мають
 * покрити кожен нетривіальний метод модуля за вимогами BL-10…BL-80.
 */
@DisplayName("ЕдуПортал — зразок юніт-тестів")
class ExampleTest {

    private GradeCalculator grades;
    private EnrollmentValidator enrollment;

    @BeforeEach
    void setUp() {
        grades = new GradeCalculator();
        enrollment = new EnrollmentValidator();
    }

    @Test
    @DisplayName("BL-10: зважений підсумок 80×40% + 90×60% = 86,00")
    void weightedTotalOfTwoItems() {
        List<GradeItem> items = List.of(
            new GradeItem("Лабораторні роботи", 80, 40),
            new GradeItem("Іспит", 90, 60));
        assertEquals(86.00, grades.weightedTotal(items), 0.001);
    }

    @Test
    @DisplayName("BL-10: сума ваг ≠ 100 — IllegalArgumentException")
    void weightsMustSumTo100() {
        List<GradeItem> items = List.of(new GradeItem("Лабораторні роботи", 80, 40));
        assertThrows(IllegalArgumentException.class, () -> grades.weightedTotal(items));
    }

    @ParameterizedTest(name = "BL-20: ectsLetter({0}) = {1}")
    @CsvSource({"97,A", "90,B", "80,C", "70,D", "62,E", "50,Fx", "20,F"})
    void ectsLetterSamples(double total, String expected) {
        assertEquals(expected, grades.ectsLetter(total));
    }

    @Test
    @DisplayName("BL-70: усі умови виконано — зарахування дозволено")
    void canEnrollWhenAllConditionsMet() {
        Course course = new Course("tzyapz", "edu2026",
            LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30), 25, 10, "prog-basics");
        Student student = new Student("s001", Map.of("prog-basics", 75.0));
        assertTrue(enrollment.canEnroll(student, course, LocalDate.of(2026, 9, 8), "edu2026"));
    }
}
