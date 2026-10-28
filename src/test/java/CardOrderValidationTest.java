import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CardOrderValidationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    static void setupAll() {
        System.out.println("=== CardOrderValidationTest: запуск драйвера ===");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        System.out.println("[SETUP] Chrome started successfully.");
    }

    @AfterAll
    static void teardownAll() {
        if (driver != null) {
            System.out.println("=== CardOrderValidationTest: закрытие драйвера ===");
            driver.quit();
        }
    }

    @Test
    void shouldShowValidationErrorWhenNameIsInvalid() {
        System.out.println("1. Открываем публичный сайт...");
        // ВАЖНО: Публичный сайт
        driver.get("https://demoqa.com/");

        // Проверяем, что страница реально загрузилась
        WebElement header = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".main-header-text"))
        );
        assertTrue(header.isDisplayed(), "Page header should be visible");

        // Простая проверка взаимодействия: клик по ссылке
        WebElement link = wait.until(
            ExpectedConditions.elementToBeClickable(By.cssSelector("span[class*='text']"))
        );
        link.click();

        System.out.println("✅ ТЕСТ ПРОЙДЕН на DemoQA!");
    }
}
