package ua.kpi.tzyapz.eduportal;

/**
 * Елемент оцінювання журналу курсу «ЕдуПортал» (BL-10).
 *
 * @param name   назва елемента (наприклад, «Лабораторна робота 1»)
 * @param score  бал студента за елемент, 0..100
 * @param weight вага елемента у підсумку курсу, відсотки (сума ваг усіх елементів = 100)
 */
public record GradeItem(String name, double score, double weight) { }
