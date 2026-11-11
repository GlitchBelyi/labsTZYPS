package ua.kpi.tzyapz.eduportal.ui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * UI-05 — автор: Білогривий (АО), задача 6.4. Сценарій записано в Selenium IDE
 * (selenium-ide/login-negative.side) і перенесено в JUnit 5 за starter/README.md, розд. 6.
 *
 * Припущення про BaseUiTest (Чупай, задача 3.2): захищені поля `driver` (WebDriver)
 * і `wait` (WebDriverWait). Якщо названо інакше — змінити два імені нижче.
 * Етика demo: пароль свідомо неправильний (облікових даних не потрібно), один вхід на тест.
 */
@Tag("ui")
class LoginNegativeUiTest extends BaseUiTest {

    private static final String LOGIN_URL = "https://sandbox.moodledemo.net/login/index.php";
    private static final By USERNAME = By.id("username");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BTN = By.id("loginbtn");
    // клас, а не текст: повідомлення на demo буває англійською (BUG-#2)
    private static final By ERROR_ALERT = By.cssSelector(".loginerrors .alert-danger");

    @Test
    @DisplayName("TC-002 / F-10: правильне ім'я, неправильний пароль → лишаємось на формі входу з помилкою")
    void wrongPasswordStaysOnLoginPage() {
        driver.get(LOGIN_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME));

        driver.findElement(USERNAME).sendKeys("student");
        driver.findElement(PASSWORD).sendKeys("Wrong-pass-9");
        driver.findElement(LOGIN_BTN).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_ALERT));

        assertAll("TC-002: негативний вхід",
            () -> assertTrue(driver.getCurrentUrl().contains("/login/"), "залишились на сторінці входу"),
            () -> assertFalse(driver.getCurrentUrl().contains("/my/"), "панель студента не відкрилась"),
            () -> assertTrue(driver.findElement(ERROR_ALERT).isDisplayed(), "є повідомлення про помилку"),
            () -> assertEquals("student", driver.findElement(USERNAME).getAttribute("value"),
                "ім'я користувача збережено у формі"),
            () -> assertEquals("", driver.findElement(PASSWORD).getAttribute("value"),
                "поле пароля очищено")
        );
    }
}
