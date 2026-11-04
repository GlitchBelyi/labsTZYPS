package ua.kpi.tzyapz.eduportal;

import java.util.Set;

/**
 * Питання тесту разом із ключем правильних відповідей (BL-40, BL-50).
 * Якщо правильний варіант один — питання «з однією правильною відповіддю»,
 * якщо кілька — «з кількома правильними відповідями».
 *
 * @param id      ідентифікатор питання (наприклад, «q1»)
 * @param options усі варіанти відповіді (ідентифікатори, наприклад «a», «b», «c», «d»)
 * @param correct правильні варіанти — непорожня підмножина {@code options}
 */
public record QuizQuestion(String id, Set<String> options, Set<String> correct) {

    public QuizQuestion {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Ідентифікатор питання порожній");
        }
        if (options == null || options.isEmpty() || correct == null || correct.isEmpty()) {
            throw new IllegalArgumentException("Питання " + id + " має містити варіанти і правильні відповіді");
        }
        if (!options.containsAll(correct)) {
            throw new IllegalArgumentException("Правильні відповіді питання " + id + " не входять до варіантів");
        }
        options = Set.copyOf(options);
        correct = Set.copyOf(correct);
    }

    /** @return true, якщо питання має рівно одну правильну відповідь */
    public boolean isSingle() {
        return correct.size() == 1;
    }
}
