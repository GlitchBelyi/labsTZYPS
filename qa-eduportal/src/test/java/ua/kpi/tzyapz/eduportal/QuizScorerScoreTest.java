package ua.kpi.tzyapz.eduportal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * BL-60 QuizScorer.score — автор: Білогривий (АО), задача 5.4.
 * Підсумок = (сума балів питань / кількість питань У КЛЮЧІ) × 100, 1 знак, HALF_UP.
 * Очікувані значення — константи з automation-plan.md, розд. 3 (SRS 1.1), не з формули коду.
 *
 * Сигнатури звірено з кодом v1 (QuizScorer.score(answers, List<QuizQuestion> key)).
 * Червоні тести тут — дефекти модуля, а не помилки автора (див. @DisplayName і audit.md).
 */
@Tag("unit")
class QuizScorerScoreTest {

    // ---- дані питань (варіанти / правильні) ----
    private static final Q SINGLE = new Q(Set.of("A", "B"), Set.of("A"));                       // правильна A
    private static final Q MULTI = new Q(Set.of("A", "B", "C", "D", "E"), Set.of("A", "B", "C")); // правильні A,B,C

    private static final Set<String> RIGHT = Set.of("A");
    private static final Set<String> WRONG = Set.of("B");

    static Stream<Arguments> scoreCases() {
        List<Q> sevenQuestions = List.of(SINGLE, SINGLE, SINGLE, SINGLE, SINGLE, SINGLE, SINGLE);
        List<Set<String>> onlyFirstAnswered = new ArrayList<>();
        onlyFirstAnswered.add(RIGHT);
        for (int i = 1; i < 7; i++) {
            onlyFirstAnswered.add(null); // немає запису у answers
        }
        return Stream.of(
            Arguments.of("2 питання: 1, 0 → 50.0", List.of(SINGLE, SINGLE), List.of(RIGHT, WRONG), 50.0),
            Arguments.of("3 питання: 1, 1, 0 → 66.7", List.of(SINGLE, SINGLE, SINGLE), List.of(RIGHT, RIGHT, WRONG), 66.7),
            Arguments.of("3 питання: 1, 0, 0 → 33.3", List.of(SINGLE, SINGLE, SINGLE), List.of(RIGHT, WRONG, WRONG), 33.3),
            Arguments.of("3 питання: 1, 1, 1 → 100.0", List.of(SINGLE, SINGLE, SINGLE), List.of(RIGHT, RIGHT, RIGHT), 100.0),
            Arguments.of("2 питання: 0, 0 → 0.0", List.of(SINGLE, SINGLE), List.of(WRONG, WRONG), 0.0),
            Arguments.of("TC-029: 1 питання, частковий 2/3 → 66.7", List.of(MULTI), List.of(Set.of("A", "B")), 66.7),
            Arguments.of("3 питання: 1, 2/3, 0 → 55.6", List.of(SINGLE, MULTI, SINGLE),
                List.of(RIGHT, Set.of("A", "B"), WRONG), 55.6),
            Arguments.of("7 питань, відповідь лише на 1 → 14.3 (знаменник за ключем)", sevenQuestions, onlyFirstAnswered, 14.3)
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("scoreCases")
    @DisplayName("BL-60: підсумок за ключем, 1 знак (HALF_UP); дробові рядки червоні через дефект #37")
    void scoreMatchesSrs(String title, List<Q> key, List<Set<String>> answers, double expected) {
        assertEquals(expected, score(key, answers), 0.001, title);
    }

    @Test
    @DisplayName("BL-60: питання ключа без запису у відповідях дає 0, а не виняток")
    void unansweredQuestionCountsAsZero() {
        // 2 питання, відповідь тільки на перше (правильна): (1 + 0) / 2 × 100 = 50.0
        List<Set<String>> answers = new ArrayList<>();
        answers.add(RIGHT);
        answers.add(null);
        assertEquals(50.0, score(List.of(SINGLE, SINGLE), answers), 0.001);
    }

    @Test
    @DisplayName("BL-60: id відповіді, якого немає в ключі → IllegalArgumentException (дефект #44)")
    void answerIdNotInKeyThrows() {
        Map<String, Q> key = new LinkedHashMap<>();
        key.put("q1", SINGLE);
        Map<String, Set<String>> answers = new LinkedHashMap<>();
        answers.put("q1", RIGHT);
        answers.put("q99", RIGHT); // у ключі такого питання немає
        assertThrows(IllegalArgumentException.class, () -> callScore(key, answers));
    }

    @Test
    @DisplayName("BL-60: порожній ключ → IllegalArgumentException")
    void emptyKeyThrows() {
        assertThrows(IllegalArgumentException.class, () -> callScore(new LinkedHashMap<>(), new LinkedHashMap<>()));
    }

    // =========================== ADAPTER ===========================
    // Єдине місце, залежне від API QuizScorer (звірено з кодом v1 у main).

    private record Q(Set<String> options, Set<String> correct) {}

    private static double score(List<Q> key, List<Set<String>> answers) {
        Map<String, Q> keyMap = new LinkedHashMap<>();
        Map<String, Set<String>> ansMap = new LinkedHashMap<>();
        for (int i = 0; i < key.size(); i++) {
            keyMap.put("q" + (i + 1), key.get(i));
            if (answers.get(i) != null) {
                ansMap.put("q" + (i + 1), answers.get(i));
            }
        }
        return callScore(keyMap, ansMap);
    }

    private static double callScore(Map<String, Q> key, Map<String, Set<String>> answers) {
        List<QuizQuestion> real = new ArrayList<>();
        key.forEach((id, q) -> real.add(new QuizQuestion(id, q.options(), q.correct())));
        return new QuizScorer().score(answers, real); // сигнатура v1: score(Map<String,Set<String>> answers, List<QuizQuestion> key)
    }
}
