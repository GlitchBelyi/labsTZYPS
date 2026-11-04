package ua.kpi.tzyapz.eduportal.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * UI-02 і UI-03: TC-004 (перелік курсів без входу) і TC-005 (пошук за відсутнім запитом).
 * Основа — записи Selenium IDE (selenium-ide/courses.side); експорт IDE на JUnit 4 тут не
 * використовується, бо проєкт на JUnit 5. Очікування — явні (WebDriverWait), без Thread.sleep.
 *
 * Запуск (тег ui за замовчуванням вимкнений):
 *   mvn test -DexcludedGroups=none -Dtest=CoursesUiTest
 */
@DisplayName("UI: перелік курсів і пошук (TC-004, TC-005)")
class CoursesUiTest extends BaseUiTest {

    /** Запит, за яким у Moodle заздалегідь немає результатів (дані тесту: вигаданий рядок). */
    private static final String NOT_EXISTING_QUERY = "T3-Zzzq-9999";

    /**
     * Повідомлення про відсутність результатів. Українське значення взято з ui-scenarios.md,
     * англійське додано на випадок, якщо сайт покаже іншу мову. Точний текст перевірити
     * на екрані й за потреби скоригувати.
     */
    private static final Pattern NO_RESULTS = Pattern.compile(
        "Результат відсутній|No results",
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    @Test
    @DisplayName("TC-004 (F-30): без входу на головній видно «My first course» і «My second course»")
    void guestSeesCourseList() {
        driver.get(BASE_URL + "/");

        WebElement first = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.linkText("My first course")));
        WebElement second = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.linkText("My second course")));

        assertTrue(first.isDisplayed(), "Посилання «My first course» не видно");
        assertTrue(second.isDisplayed(), "Посилання «My second course» не видно");
    }

    @Test
    @DisplayName("TC-005: пошук за неіснуючим рядком T3-Zzzq-9999 показує повідомлення про відсутність результатів")
    void searchWithoutMatchesShowsMessage() {
        // lang=uk робить мову інтерфейсу однаковою для будь-якого браузера
        driver.get(BASE_URL + "/search/index.php?lang=uk");

        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("input[id^='searchinput']")));
        input.sendKeys(NOT_EXISTING_QUERY, Keys.ENTER);

        try {
            wait.until(d -> NO_RESULTS.matcher(
                d.findElement(By.tagName("body")).getText()).find());
        } catch (TimeoutException e) {
            String text = driver.findElement(By.tagName("body")).getText();
            fail("Повідомлення про відсутність результатів не з'явилося. Початок тексту сторінки: "
                + text.substring(0, Math.min(300, text.length())));
        }
    }
}
