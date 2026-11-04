package ua.kpi.tzyapz.eduportal;

import java.time.LocalDate;

/**
 * Курс із параметрами самозарахування (BL-70).
 *
 * @param id             ідентифікатор курсу
 * @param enrolmentKey   кодове слово самозарахування
 * @param enrolStart     перший день періоду зарахування (включно)
 * @param enrolEnd       останній день періоду зарахування (включно)
 * @param capacity       місткість курсу (максимальна кількість зарахованих)
 * @param enrolled       кількість уже зарахованих студентів
 * @param prerequisiteId ідентифікатор передумовного курсу або {@code null}, якщо передумови немає
 */
public record Course(String id, String enrolmentKey, LocalDate enrolStart, LocalDate enrolEnd,
                     int capacity, int enrolled, String prerequisiteId) { }
