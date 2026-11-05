package ua.kpi.tzyapz.eduportal;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
 
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
 
/**
 * Юніт-тести GradeCalculator.weightedTotal (BL-10). Автор: Чупай.
 * Очікувані значення взято з SRS 1.1 і automation-plan.md (розд. 3), а не з коду модуля.
 * Окремий клас, щоб не конфліктувати з GradeCalculatorTest (BL-20, BL-30, Ромашко).
 */
class GradeCalculatorBl10Test {
 
    private static final double DELTA = 0.001;
 
    private final GradeCalculator calc = new GradeCalculator();
 
    private static GradeItem item(double score, double weight) {
        return new GradeItem("E", score, weight);
    }
 
    @Test
    @DisplayName("BL-10: 80/30 і 60/70 дають підсумок 66,00 (TC-045)")
    void typicalCase() {
        assertEquals(66.00, calc.weightedTotal(List.of(item(80, 30), item(60, 70))), DELTA);
    }
 
    @Test
    @DisplayName("BL-10: один елемент 100/100 дає максимум 100,00")
    void maximum() {
        assertEquals(100.00, calc.weightedTotal(List.of(item(100, 100))), DELTA);
    }
 
    @Test
    @DisplayName("BL-10: один елемент 0/100 дає мінімум 0,00")
    void minimum() {
        assertEquals(0.00, calc.weightedTotal(List.of(item(0, 100))), DELTA);
    }
 
    @Test
    @DisplayName("BL-10: округлення HALF_UP, 0.005 дає 0,01")
    void roundingHalfUp() {
        assertEquals(0.01, calc.weightedTotal(List.of(item(0.01, 50), item(0, 50))), DELTA);
    }
 
    @Test
    @DisplayName("BL-10: сума ваг 99 викликає IllegalArgumentException")
    void weightsSum99() {
        assertThrows(IllegalArgumentException.class,
                () -> calc.weightedTotal(List.of(item(50, 50), item(50, 49))));
    }
 
    @Test
    @DisplayName("BL-10: сума ваг 101 викликає IllegalArgumentException")
    void weightsSum101() {
        assertThrows(IllegalArgumentException.class,
                () -> calc.weightedTotal(List.of(item(50, 50), item(50, 51))));
    }
 
    @Test
    @DisplayName("BL-10: від'ємна вага при сумі 100 викликає IllegalArgumentException")
    void negativeWeight() {
        assertThrows(IllegalArgumentException.class,
                () -> calc.weightedTotal(List.of(item(50, 110), item(50, -10))));
    }
 
    @Test
    @DisplayName("BL-10: порожній список викликає IllegalArgumentException")
    void emptyList() {
        assertThrows(IllegalArgumentException.class, () -> calc.weightedTotal(List.of()));
    }
 
    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("validCases")
    @DisplayName("BL-10: допустимі дані дають підсумок за SRS")
    void validData(String description, double expected, List<GradeItem> items) {
        assertEquals(expected, calc.weightedTotal(items), DELTA);
    }
 
    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidScores")
    @DisplayName("BL-10: бал поза 0..100 викликає IllegalArgumentException")
    void invalidScore(String description, List<GradeItem> items) {
        assertThrows(IllegalArgumentException.class, () -> calc.weightedTotal(items));
    }
 
    static Stream<Arguments> validCases() {
        return Stream.of(
                // дробові бали: 42.775 + 35.025 = 77.80
                Arguments.of("85.55/50 і 70.05/50", 77.80,
                        List.of(item(85.55, 50), item(70.05, 50))),
                // 18.875 + 20.5625 + 45.5 = 84.9375, округлення до 84,94
                Arguments.of("75.5/25, 82.25/25, 91/50", 84.94,
                        List.of(item(75.5, 25), item(82.25, 25), item(91, 50))),
                // три елементи: 18 + 21 + 25 = 64
                Arguments.of("90/20, 70/30, 50/50", 64.00,
                        List.of(item(90, 20), item(70, 30), item(50, 50))),
                // дробові ваги, сума 100: виняток не очікується
                Arguments.of("100/33.3, 100/33.3, 100/33.4", 100.00,
                        List.of(item(100, 33.3), item(100, 33.3), item(100, 33.4))));
    }
 
    static Stream<Arguments> invalidScores() {
        return Stream.of(
                Arguments.of("бал -0.01 (нижче межі)", List.of(item(-0.01, 100))),
                Arguments.of("бал 100.01 (вище межі)", List.of(item(100.01, 100))));
    }
}
 
