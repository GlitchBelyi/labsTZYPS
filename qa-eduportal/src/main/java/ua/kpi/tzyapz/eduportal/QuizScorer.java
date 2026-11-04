package ua.kpi.tzyapz.eduportal;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Оцінювання спроби тесту «ЕдуПортал» (вимоги BL-40…BL-60).
 * Бал за питання — у межах 0..1, підсумок спроби — у відсотках з округленням до 1 знака (HALF_UP).
 */
public class QuizScorer {

    /**
     * Бал за одне питання (BL-40, BL-50).
     * Питання з однією правильною відповіддю: 1, якщо обрано саме правильний варіант, інакше 0.
     * Питання з кількома правильними: частка обраних правильних мінус частка обраних неправильних,
     * але не менше 0.
     *
     * @param question питання з ключем
     * @param selected обрані студентом варіанти; {@code null} або порожня множина — відповіді немає
     * @return бал 0..1
     * @throws IllegalArgumentException якщо обрано варіант, якого немає серед варіантів питання
     */
    public double questionScore(QuizQuestion question, Set<String> selected) {
        if (question == null) {
            throw new IllegalArgumentException("Питання не задано");
        }
        if (selected == null || selected.isEmpty()) {
            return 0.0;
        }
        if (!question.options().containsAll(selected)) {
            throw new IllegalArgumentException("Обрано варіант, якого немає у питанні " + question.id());
        }
        if (question.isSingle()) {
            return selected.equals(question.correct()) ? 1.0 : 0.0;
        }
        long correctChosen = selected.stream().filter(question.correct()::contains).count();
        long wrongChosen = selected.size() - correctChosen;
        int wrongTotal = question.options().size() - question.correct().size();
        double correctShare = (double) correctChosen / question.correct().size();
        double wrongShare = wrongTotal == 0 ? 0.0 : (double) wrongChosen / wrongTotal;
        return correctShare - wrongShare;
    }

    /**
     * Підсумок спроби у відсотках (BL-60): сума балів за питаннями / кількість питань у ключі × 100.
     *
     * @param answers відповіді студента: ідентифікатор питання → обрані варіанти
     * @param key     ключ тесту — перелік питань із правильними відповідями
     * @return відсоток 0..100, округлений до 1 знака
     * @throws IllegalArgumentException якщо ключ порожній
     */
    public double score(Map<String, Set<String>> answers, List<QuizQuestion> key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Ключ тесту порожній");
        }
        Map<String, Set<String>> given = answers == null ? Map.of() : answers;
        double points = 0.0;
        for (QuizQuestion question : key) {
            points += questionScore(question, given.get(question.id()));
        }
        double percent = points / key.size() * 100.0;
        return (double) Math.round(percent);
    }
}
