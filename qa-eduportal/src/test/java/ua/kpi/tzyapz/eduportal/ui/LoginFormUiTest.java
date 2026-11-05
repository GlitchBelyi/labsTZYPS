package ua.kpi.tzyapz.eduportal.ui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * UI-04 — автор: Александрова (СМ), задача 6.3. Кейс TC-017, вимога GUI-10.
 * Припущення про BaseUiTest (Чупай, задача 3.2): захищені поля `driver` і `wait`.
 * Етика demo: тест лише відкриває форму входу, нічого не вводить і не натискає.
 */
@Tag("ui")
class LoginFormUiTest extends BaseUiTest {

    private static final String LOGIN_URL = "https://sandbox.moodledemo.net/login/index.php";

    private static final By USERNAME = By.id("username");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BTN = By.id("loginbtn");
    private static final By GUEST_BTN = By.id("loginguestbtn");
    private static final By FORGOT_LINK = By.cssSelector("a[href*='forgot_password']");
    private static final By PASSWORD_TOGGLE = By.cssSelector("[data-action='togglesensitive'], .toggle-sensitive-btn");
    private static final By LANGUAGE_MENU = By.cssSelector("a[data-bs-toggle='dropdown'][aria-haspopup='true']");

    @BeforeEach
    void openLoginForm() {
        driver.get(LOGIN_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME));
    }

    @Test
    @DisplayName("TC-017 / GUI-10: форма входу містить обидва поля, кнопку входу, гостьовий вхід, "
        + "посилання відновлення пароля, перемикач показу пароля і мови")
    void loginFormHasExpectedElements() {
        // Чекаємо, поки з'являться всі критичні елементи, щоб уникнути передчасних перевірок
        wait.until(ExpectedConditions.presenceOfElementLocated(LOGIN_BTN));
        wait.until(ExpectedConditions.presenceOfElementLocated(PASSWORD_TOGGLE)); 
        wait.until(ExpectedConditions.presenceOfElementLocated(LANGUAGE_MENU)); 
        
        assertAll("TC-017: склад форми входу",
            () -> assertTrue(driver.findElement(USERNAME).isDisplayed(), "поле імені входу"),
            () -> assertEquals("password", driver.findElement(PASSWORD).getAttribute("type"),
                "символи пароля приховані"),
            () -> assertTrue(driver.findElement(LOGIN_BTN).isDisplayed(), "кнопка входу"),
            () -> assertFalse(driver.findElements(GUEST_BTN).isEmpty(), "кнопка гостьового входу"),
            () -> assertFalse(driver.findElements(FORGOT_LINK).isEmpty(), "посилання «Забули пароль?»"),
            () -> assertFalse(driver.findElements(PASSWORD_TOGGLE).isEmpty(), "перемикач показу пароля"),
            () -> assertFalse(driver.findElements(LANGUAGE_MENU).isEmpty(), "перемикач мови")
        );
    }

    // Очікувано червоний на demo: написи англійською. Відомий дефект, Issue #2 (нового Issue не створюємо).
    @Test
    @DisplayName("TC-017 / GUI-10 / Issue #2: написи за SRS 1.1: «З поверненням», «Забули пароль?», «Увійти як гість»")
    void loginFormTextsMatchSrs() {
        String pageText = driver.findElement(By.tagName("body")).getText();
        assertAll("TC-017: тексти форми входу",
            () -> assertTrue(pageText.contains("З поверненням"), "заголовок «З поверненням»"),
            () -> assertTrue(pageText.contains("Забули пароль?"), "посилання «Забули пароль?»"),
            () -> assertTrue(pageText.contains("Увійти як гість"), "кнопка «Увійти як гість»")
        );
    }
}
