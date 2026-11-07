package ua.kpi.tzyapz.eduportal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Розрахунок підсумкової оцінки курсу «ЕдуПортал» (вимоги BL-10…BL-30).
 * Усі результати — {@code double}, округлені до 2 знаків після коми за правилом HALF_UP.
 */
public class GradeCalculator {

    private static final double WEIGHT_SUM = 100.0;
    private static final double EPS = 1e-9;

    /**
     * Зважений підсумок курсу (BL-10): сума добутків бал × вага / 100.
     *
     * @param items елементи оцінювання; сума ваг має дорівнювати 100, бали — у межах 0..100
     * @return підсумок 0..100, округлений до 2 знаків
     * @throws IllegalArgumentException якщо перелік порожній, сума ваг не дорівнює 100
     *                                  або бал поза межами 0..100
     */
    public double weightedTotal(List<GradeItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Перелік елементів оцінювання порожній");
        }
        double weightSum = 0.0;
        double total = 0.0;
        for (GradeItem item : items) {
            if (item.score() < 0 || item.score() > 100) {
                throw new IllegalArgumentException("Бал поза межами 0..100: " + item.name());
            }
            if (item.weight() < 0) {
                throw new IllegalArgumentException("Від'ємна вага елемента: " + item.name());
            }
            weightSum += item.weight();
            total += item.score() * item.weight() / 100.0;
        }
        if (Math.abs(weightSum - WEIGHT_SUM) > EPS) {
            throw new IllegalArgumentException("Сума ваг має дорівнювати 100, а не " + weightSum);
        }
        return round2(total);
    }

    /**
     * Літера шкали ECTS за підсумком курсу (BL-20).
     *
     * @param total підсумок курсу, 0..100
     * @return одна з літер A, B, C, D, E, Fx, F
     * @throws IllegalArgumentException якщо підсумок поза межами 0..100
     */
    public String ectsLetter(double total) {
        if (total < 0 || total > 100) {
            throw new IllegalArgumentException("Підсумок поза межами 0..100: " + total);
        }
        if (total > 95) {
            return "A";
        }
        if (total >= 85) {
            return "B";
        }
        if (total >= 75) {
            return "C";
        }
        if (total >= 65) {
            return "D";
        }
        if (total >= 60) {
            return "E";
        }
        if (total >= 35) {
            return "Fx";
        }
        return "F";
    }

    /**
     * Бал після штрафу за запізнення (BL-30): −10 % від балу за кожен день, але не більше −50 %.
     *
     * @param score    бал за роботу, 0..100
     * @param daysLate кількість повних днів запізнення, 0 — здано вчасно
     * @return бал після штрафу, округлений до 2 знаків
     * @throws IllegalArgumentException якщо бал поза межами 0..100 або daysLate від'ємне
     */
    public double latePenalty(double score, int daysLate) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Бал поза межами 0..100: " + score);
        }
        if (daysLate < 0) {
            throw new IllegalArgumentException("Кількість днів запізнення не може бути від'ємною");
        }
        int cappedDays = Math.min(daysLate, 5);
        return round2(score * Math.pow(0.9, cappedDays));
    }

    private static double round2(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
