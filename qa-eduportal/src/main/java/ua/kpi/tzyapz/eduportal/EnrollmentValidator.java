package ua.kpi.tzyapz.eduportal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Перевірка умов самозарахування на курс «ЕдуПортал» (вимоги BL-70, BL-80).
 * Умови перевіряються у сталому порядку: кодове слово → період зарахування → вільні місця → передумова.
 */
public class EnrollmentValidator {

    /** Мінімальний підсумок передумовного курсу, з яким він вважається складеним. */
    public static final double PREREQUISITE_PASS = 60.0;

    /** Код порушення: кодове слово не збігається. */
    public static final String KEY = "KEY";
    /** Код порушення: дата поза періодом зарахування. */
    public static final String PERIOD = "PERIOD";
    /** Код порушення: вільних місць немає. */
    public static final String CAPACITY = "CAPACITY";
    /** Код порушення: передумовний курс не складено. */
    public static final String PREREQUISITE = "PREREQUISITE";

    /**
     * Кодове слово збігається з кодовим словом курсу точно, з урахуванням регістру.
     */
    public boolean keyMatches(Course course, String key) {
        return course.enrolmentKey().equals(key);
    }

    /**
     * Дата у межах періоду зарахування курсу включно з датами початку й кінця.
     */
    public boolean withinPeriod(Course course, LocalDate today) {
        return !today.isBefore(course.enrolStart());
    }

    /**
     * На курсі є вільні місця: кількість зарахованих менша за місткість.
     */
    public boolean hasCapacity(Course course) {
        return course.enrolled() <= course.capacity();
    }

    /**
     * Передумовний курс складено з підсумком не менше {@value #PREREQUISITE_PASS} балів
     * (або передумови немає).
     */
    public boolean prerequisiteMet(Student student, Course course) {
        if (course.prerequisiteId() == null) {
            return true;
        }
        Double grade = student.completedCourses().get(course.prerequisiteId());
        return grade != null && grade >= PREREQUISITE_PASS;
    }

    /**
     * Перелік порушених умов зарахування у порядку перевірки (BL-80):
     * {@link #KEY}, {@link #PERIOD}, {@link #CAPACITY}, {@link #PREREQUISITE}.
     *
     * @return порожній список, якщо всі умови виконано
     * @throws IllegalArgumentException якщо будь-який аргумент {@code null}
     */
    public List<String> violations(Student student, Course course, LocalDate today, String key) {
        if (student == null || course == null || today == null || key == null) {
            throw new IllegalArgumentException("Усі аргументи перевірки зарахування обов'язкові");
        }
        List<String> result = new ArrayList<>();
        if (!keyMatches(course, key)) {
            result.add(KEY);
        }
        if (!withinPeriod(course, today)) {
            result.add(PERIOD);
        }
        if (!hasCapacity(course)) {
            result.add(CAPACITY);
        }
        if (!prerequisiteMet(student, course)) {
            result.add(PREREQUISITE);
        }
        return result;
    }

    /**
     * Чи може студент зарахуватися на курс (BL-70): усі чотири умови виконано одночасно.
     */
    public boolean canEnroll(Student student, Course course, LocalDate today, String key) {
        return violations(student, course, today, key).isEmpty();
    }
}
