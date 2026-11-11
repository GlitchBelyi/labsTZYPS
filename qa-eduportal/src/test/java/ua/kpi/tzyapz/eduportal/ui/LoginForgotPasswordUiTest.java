package ua.kpi.tzyapz.eduportal.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

/**
 * UI-08 / TC-018: посилання «Забули пароль?» на сторінці входу. Автор: Мележик (КТ).
 * Сторінку не змінюємо, акаунт не використовуємо, форму відновлення не надсилаємо;
 * лише відкриваємо сторінку входу й переходимо за посиланням (етика 1.3).
 * Локатор не залежить від мови інтерфейсу: шукаємо за href.
 */
@Tag("ui")
@DisplayName("UI-08 / TC-018: посилання «Забули пароль?»")
class LoginForgotPasswordUiTest extends BaseUiTest {

    private static final By FORGOT_LINK = By.cssSelector("a[href*='/login/forgot_password.php']");

    @Test
    @DisplayName("TC-018: клік на «Забули пароль?» відкриває /login/forgot_password.php")
    void forgotPasswordLinkOpensRecoveryPage() {
        driver.get(BASE_URL + "/login/index.php");
        wait.until(d -> !d.findElements(FORGOT_LINK).isEmpty());
        driver.findElement(FORGOT_LINK).click();
        wait.until(d -> d.getCurrentUrl().contains("/login/forgot_password.php"));
        String url = driver.getCurrentUrl();
        assertTrue(url.contains("/login/forgot_password.php"),
                "Після кліку не відкрилась сторінка відновлення пароля: " + url);
    }
}
