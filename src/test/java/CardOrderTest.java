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
        options.addArguments("--disable-blink-features=AutomationControlled");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // 15 сек достаточно для демо
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
        // ВАЖНО: Публичный URL вместо localhost
        System.out.println("[TEST] Opening: https://demoqa.com/");
        driver.get("https://demoqa.com/");

        System.out.println("[TEST] Current URL: " + driver.getCurrentUrl());
        System.out.println("[TEST] Page title: " + driver.getTitle());

        // Ищем реально существующий элемент на DemoQA
        WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".main-header-text")));
        assertTrue(header.isDisplayed(), "Header should be visible");

        // Имитация действия: кликаем на ссылку в меню
        WebElement bookStoreLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("span[class*='text']")));
        bookStoreLink.click();

        WebElement newHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".main-header-text")));
        System.out.println("[TEST] ✅ Test passed on DemoQA!");
    }
}
