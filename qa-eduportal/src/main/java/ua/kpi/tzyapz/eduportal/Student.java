package ua.kpi.tzyapz.eduportal;

import java.util.Map;

/**
 * Студент для перевірки умов зарахування (BL-70).
 *
 * @param id               ідентифікатор студента
 * @param completedCourses підсумкові оцінки (0..100) за вже складені курси: ідентифікатор курсу → бал
 */
public record Student(String id, Map<String, Double> completedCourses) {

    public Student {
        completedCourses = completedCourses == null ? Map.of() : Map.copyOf(completedCourses);
    }
}
