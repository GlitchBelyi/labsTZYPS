package ua.kpi.tzyapz.eduportal.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UI-07 / TC-019: заголовок вкладки (Title) на сторінці входу. Автор: Мележик (КТ).
 * Сторінку не змінюємо, акаунт не використовуємо; один запит на тест (етика 1.3).
 * Мова demo нестабільна, тому допускаємо англійський і український варіант.
 */
@Tag("ui")
@DisplayName("UI-07 / TC-019: заголовок сторінки входу")
class LoginTitleUiTest extends BaseUiTest {

    @Test
    @DisplayName("TC-019: Title сторінки входу містить «Log in» або «Увійти»")
    void loginPageTitleContainsLogIn() {
        driver.get(BASE_URL + "/login/index.php");
        wait.until(d -> !d.getTitle().isBlank());
        String title = driver.getTitle();
        assertTrue(title.contains("Log in") || title.contains("Увійти"),
                "Заголовок вкладки не містить «Log in» або «Увійти»: " + title);
    }
}
