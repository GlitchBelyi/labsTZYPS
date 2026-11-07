package ua.kpi.tzyapz.eduportal.ui;
 
import static org.junit.jupiter.api.Assertions.assertTrue;
 
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
 
/**
 * UI-01 (TC-001, F-10): вхід студента. Очікувано червоний: на demo після входу
 * відкривається головна сторінка «/», а не панель «/my/» (відомий дефект demo, BUG-#1).
 */
@Tag("ui")
class LoginUiTest extends BaseUiTest {
 
    @Test
    @DisplayName("UI-01 / TC-001 (F-10): після входу student відкривається панель /my/; "
            + "перевіряється лише перехід на /my/, наявність «Мої курси» не перевіряється (BUG-#1)")
    void studentLoginOpensDashboard() {
        driver.get(BASE_URL + "/login/index.php");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")))
                .sendKeys("student");
        driver.findElement(By.id("password")).sendKeys("sandbox24");
        driver.findElement(By.id("loginbtn")).click();
 
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login/")));
 
        String url = driver.getCurrentUrl();
        assertTrue(url.contains("/my/"),
                "Очікувалась інформаційна панель (/my/), а відкрито: " + url);
    }
}
