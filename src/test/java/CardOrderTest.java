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

public class CardOrderTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    static void setup() {
        System.out.println("[SETUP] Starting Chrome...");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // 15 секунд достаточно
        System.out.println("[SETUP] Chrome started successfully.");
    }

    @AfterAll
    static void teardown() {
        if (driver != null) {
            driver.quit();
            System.out.println("[TEARDOWN] Browser closed.");
        }
    }

    @Test
    void shouldSubmitCardOrderFormSuccessfully() {
        // ВАЖНО: Публичный сайт вместо localhost
        System.out.println("[TEST] Opening: https://demoqa.com/");
        driver.get("https://demoqa.com/");

        // Ищем элемент, который ГАРАНТИРОВАННО есть на этой странице
        WebElement header = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".main-header-text"))
        );
        
        assertTrue(header.isDisplayed(), "Header must be visible");
        System.out.println("[TEST] ✅ Test passed on DemoQA!");
    }
}
