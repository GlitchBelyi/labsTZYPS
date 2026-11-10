package ua.kpi.tzyapz.eduportal.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

/**
 * UI-06 / TC-042 (GUI-75): верхня панель гостя. Автор: Мележик (КТ).
 * Сторінку не змінюємо, акаунт не використовуємо; один запит на тест (етика 1.3).
 * Локатори не залежать від мови: id, href, структура.
 * Червоні перевірки посилаються на відомі дефекти demo зі спринта 3 (#5, #6).
 */
@Tag("ui")
@DisplayName("UI-06 / TC-042: верхня панель гостя")
class NavigationUiTest extends BaseUiTest {

    @Test
    @DisplayName("TC-042: гість бачить навігаційну панель із посиланням «Увійти» (/login/index.php)")
    void guestSeesNavbarAndLoginLink() {
        driver.get(BASE_URL + "/");
        Boolean navbarShown = wait.until(d -> !d.findElements(By.cssSelector("nav.navbar")).isEmpty());
        assertTrue(navbarShown, "Навігаційна панель відсутня");
        assertFalse(driver.findElements(By.cssSelector("a[href*='/login/index.php']")).isEmpty(),
                "Немає посилання на форму входу");
    }

    @Test
    @DisplayName("TC-042: гість не бачить меню користувача (#user-menu-toggle) — відомі дефекти #5, #6")
    void guestHasNoUserMenu() {
        driver.get(BASE_URL + "/");
        wait.until(d -> !d.findElements(By.cssSelector("nav.navbar")).isEmpty());
        assertTrue(driver.findElements(By.id("user-menu-toggle")).isEmpty(),
                "Гість бачить меню користувача (#5, #6)");
    }

    @Test
    @DisplayName("TC-042: перемикач мови (#lang-menu-toggle) присутній на панелі гостя")
    void languageSwitcherPresent() {
        driver.get(BASE_URL + "/");
        wait.until(d -> !d.findElements(By.cssSelector("nav.navbar")).isEmpty());
        assertFalse(driver.findElements(By.id("lang-menu-toggle")).isEmpty(),
                "Немає перемикача мови");
    }
}
