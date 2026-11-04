package ua.kpi.tzyapz.eduportal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Юніт-тести GradeCalculator: BL-20 (ectsLetter) і BL-30 (latePenalty).
 * Очікувані значення взято з SRS 1.1, а не з поточної поведінки коду.
 */

@DisplayName("GradeCalculator")
class GradeCalculatorTest {

    /** Допуск для порівняння double: результат округлюється до 2 знаків. */
    private static final double EPS = 0.001;

    private GradeCalculator calc;

    @BeforeEach
    void setUp() {
        calc = new GradeCalculator();
    }

    @Nested
    @DisplayName("BL-20: ectsLetter — літера шкали ECTS")
    class EctsLetter {

        // Значення на межах кожної смуги та поруч із ними; 94.5 — число між 94 і 95,
        // яке в SRS 1.0 не належало жодній смузі (дефект вимоги №18, виправлений у SRS 1.1).
        // Межу 95 винесено в окремий тест, щоб його падіння мало одну причину.
        @ParameterizedTest(name = "BL-20: ectsLetter({0}) = {1}")
        @CsvSource({
            "100, A",
            "94.99, B", "94.5, B", "85, B",
            "84.99, C", "75, C",
            "74.99, D", "65, D",
            "64.99, E", "60, E",
            "59.99, Fx", "35, Fx",
            "34.99, F", "0, F"
        })
        void letterInsideAndAtBandEdges(double total, String expected) {
            assertEquals(expected, calc.ectsLetter(total));
        }

        // Очікувано падає на v1: ectsLetter(95) повертає B (умова «> 95» замість «>= 95»).
        // Звіт про дефект: номер Issue #25
        @Test
        @DisplayName("BL-20: 95 → A (нижня межа A входить у смугу)")
        void lowerBoundOfAIsInclusive() {
            assertEquals("A", calc.ectsLetter(95.0));
        }

        // Якщо замовник змінить ставку, зміняться очікування в цьому класі, а не оцінка модуля як дефектного
        @ParameterizedTest(name = "BL-20: ectsLetter({0}) → IllegalArgumentException")
        @ValueSource(doubles = {-0.01, 100.01})
        void totalOutOfRangeThrows(double total) {
            assertThrows(IllegalArgumentException.class, () -> calc.ectsLetter(total));
        }
    }

    @Nested
    @DisplayName("BL-30: latePenalty — штраф за запізнення")
    class LatePenalty {

        @ParameterizedTest(name = "BL-30: бал {0}, запізнення {1} діб → {2}")
        @CsvSource({"80, 0, 80.00", "100, 0, 100.00", "0, 0, 0.00"})
        void noPenaltyWhenSubmittedOnTime(double score, int daysLate, double expected) {
            assertEquals(expected, calc.latePenalty(score, daysLate), EPS);
        }

        @ParameterizedTest(name = "BL-30: бал {0}, 1 доба → {2}")
        @CsvSource({"80, 1, 72.00", "100, 1, 90.00", "50, 1, 45.00"})
        void oneDayTakesTenPercent(double score, int daysLate, double expected) {
            assertEquals(expected, calc.latePenalty(score, daysLate), EPS);
        }

        // Очікувано падає на v1: штраф рахується складним відсотком (score * 0.9^days),
        // а за SRS — лінійно: score * (1 - 0.1 * days). Звіт про дефект: номер Issue #26
        @ParameterizedTest(name = "BL-30: бал {0}, {1} діб → {2} (лінійно, не складний відсоток)")
        @CsvSource({"80, 2, 64.00", "80, 4, 48.00", "100, 3, 70.00", "60, 3, 42.00"})
        void penaltyIsLinearNotCompound(double score, int daysLate, double expected) {
            assertEquals(expected, calc.latePenalty(score, daysLate), EPS);
        }

        // Очікувано падає на v1 з тієї самої причини. Звіт про дефект: Issue #26
        @ParameterizedTest(name = "BL-30: бал {0}, {1} діб → {2} (межа −50 %)")
        @CsvSource({"80, 5, 40.00", "80, 6, 40.00", "80, 100, 40.00", "100, 5, 50.00", "50, 5, 25.00"})
        void penaltyIsCappedAtFiftyPercent(double score, int daysLate, double expected) {
            assertEquals(expected, calc.latePenalty(score, daysLate), EPS);
        }

        // Усі точні значення біля межі округлення мають 3 знаки після коми, останній 5 або менше:
        // 33.35 * 0.9 = 30.015 → 30.02; 5.55 * 0.9 = 4.995 → 5.00 (HALF_UP).
        @ParameterizedTest(name = "BL-30: бал {0}, 1 доба → {2} (округлення HALF_UP до 2 знаків)")
        @CsvSource({"33.35, 1, 30.02", "16.65, 1, 14.99", "1.05, 1, 0.95", "5.55, 1, 5.00", "33.33, 1, 30.00"})
        void resultIsRoundedHalfUpToTwoDigits(double score, int daysLate, double expected) {
            assertEquals(expected, calc.latePenalty(score, daysLate), 0.001);
        }

        @ParameterizedTest(name = "BL-30: бал {0}, запізнення {1} → IllegalArgumentException")
        @CsvSource({"-0.01, 0", "100.01, 0", "50, -1"})
        void invalidInputThrows(double score, int daysLate) {
            assertThrows(IllegalArgumentException.class, () -> calc.latePenalty(score, daysLate));
        }
    }
}
