package ua.kpi.tzyapz.eduportal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Юніт-тести QuizScorer.questionScore: BL-40 (одна правильна) і BL-50 (кілька правильних).
 * Очікувані значення розраховано вручну за SRS 1.1, а не за поведінкою коду.
 * Автор: Александрова (СМ), задача 5.3.
 */
@Tag("unit")
@DisplayName("QuizScorer.questionScore — BL-40, BL-50")
class QuizScorerTest {

    /**
     * Допуск для double. Частковий бал може бути 0.6667 або 0.67 залежно від відповіді
     * замовника на питання Q2 (automation-plan.md, розд. 4), тому порівнюємо з допуском 0.001.
     */
    private static final double DELTA = 0.001;

    private QuizScorer scorer;

    @BeforeEach
    void setUp() {
        scorer = new QuizScorer();
    }

    /** "A,B" -> {"A","B"}; "" -> порожня множина. */
    private static Set<String> set(String csv) {
        if (csv == null || csv.isBlank()) {
            return Set.of();
        }
        return new HashSet<>(Arrays.asList(csv.split(",")));
    }

    @Nested
    @DisplayName("BL-40: питання з однією правильною відповіддю (варіанти A–D, правильний B)")
    class SingleCorrect {

        private final QuizQuestion q = new QuizQuestion("q1", set("A,B,C,D"), set("B"));

        @ParameterizedTest(name = "BL-40: обрано {0} → {1}")
        @CsvSource({
            "B,     1.0",
            "A,     0.0",
            "'B,C', 0.0",
            "'A,B', 0.0"
        })
        void scoreIsOneOnlyForExactlyTheCorrectOption(String selected, double expected) {
            assertEquals(expected, scorer.questionScore(q, set(selected)), DELTA);
        }

        @Test
        @DisplayName("BL-40: порожній вибір → 0 балів")
        void emptySelectionScoresZero() {
            assertEquals(0.0, scorer.questionScore(q, Set.of()), DELTA);
        }

        @Test
        @DisplayName("BL-40: null (відповіді немає) → 0 балів")
        void nullSelectionScoresZero() {
            assertEquals(0.0, scorer.questionScore(q, null), DELTA);
        }

        @Test
        @DisplayName("BL-40: варіант X, якого немає у питанні → IllegalArgumentException")
        void unknownOptionThrows() {
            assertThrows(IllegalArgumentException.class, () -> scorer.questionScore(q, set("X")));
        }
    }

    @Nested
    @DisplayName("BL-50: кілька правильних (варіанти A–E, правильні A,B,C; неправильних 2)")
    class MultipleCorrect {

        private final QuizQuestion q = new QuizQuestion("q2", set("A,B,C,D,E"), set("A,B,C"));

        // Розрахунок вручну: (обрані правильні / 3) − (обрані неправильні / 2), не менше 0.
        @ParameterizedTest(name = "BL-50: обрано {0} → {1}")
        @CsvSource({
            "'A,B,C',     1.0",     // 3/3 − 0
            "'A,B',       0.6667",  // 2/3 − 0
            "A,           0.3333",  // 1/3 − 0
            "'A,B,D',     0.1667",  // 2/3 − 1/2
            "'A,B,C,D,E', 0.0"      // 3/3 − 2/2
        })
        void partialScoreByFormula(String selected, double expected) {
            assertEquals(expected, scorer.questionScore(q, set(selected)), DELTA);
        }

        // Відокремлено, щоб падіння мало одну причину: бал не може бути від'ємним (BL-50 «не менше 0»).
        // Очікувано падає на v1, якщо questionScore не обрізає від'ємний результат.
        // Дефект не залежить від Q2. Звіт про дефект: Issue #28
        @ParameterizedTest(name = "BL-50: обрано {0} → 0.0 (бал не менший за 0)")
        @CsvSource({
            "'A,D'",   // 1/3 − 1/2 < 0
            "'D,E'"    // 0 − 2/2 < 0
        })
        void scoreIsNeverNegative(String selected) {
            assertEquals(0.0, scorer.questionScore(q, set(selected)), DELTA);
        }

        @Test
        @DisplayName("BL-50: неправильних варіантів немає → друга частка 0; {A} з правильних {A,B} = 0.5")
        void noWrongOptionsSecondShareIsZero() {
            QuizQuestion onlyCorrectOptions = new QuizQuestion("q3", set("A,B"), set("A,B"));
            assertEquals(0.5, scorer.questionScore(onlyCorrectOptions, set("A")), DELTA);
        }

        @Test
        @DisplayName("BL-50: порожній вибір → 0 балів")
        void emptySelectionScoresZero() {
            assertEquals(0.0, scorer.questionScore(q, Set.of()), DELTA);
        }

        @Test
        @DisplayName("BL-50: серед обраних є невідомий варіант X → IllegalArgumentException")
        void unknownOptionThrows() {
            assertThrows(IllegalArgumentException.class, () -> scorer.questionScore(q, set("A,X")));
        }

        @Test
        @DisplayName("BL-50: порожня множина правильних → IllegalArgumentException")
        void emptyCorrectSetThrows() {
            // у v1 виняток кидає конструктор QuizQuestion, а не questionScore: поведінка за SRS та сама
            assertThrows(IllegalArgumentException.class,
                () -> scorer.questionScore(new QuizQuestion("q4", set("A,B"), Set.of()), set("A")));
        }
    }
}
