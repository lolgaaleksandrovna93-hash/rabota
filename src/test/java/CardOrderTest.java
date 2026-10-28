import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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
        String driverPath = System.getenv("WEBDRIVER_CHROME_DRIVER");
        if (driverPath != null && !driverPath.isEmpty()) {
            System.setProperty("webdriver.chrome.driver", driverPath);
            System.out.println("[SETUP] Using driver from env: " + driverPath);
        } else {
            System.out.println("[SETUP] Driver path not set — relying on Selenium Manager.");
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-blink-features=AutomationControlled");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(60));
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
        System.out.println("[TEST] Opening page: http://localhost:9999");
        driver.get("http://localhost:9999");

        System.out.println("[TEST] Current URL: " + driver.getCurrentUrl());
        System.out.println("[TEST] Page title: " + driver.getTitle());

        var nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id=\"name\"] input")));
        System.out.println("[TEST] Name input found.");
        nameInput.clear();
        nameInput.sendKeys("Иванов Иван Иванович");

        var phoneInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id=\"phone\"] input")));
        System.out.println("[TEST] Phone input found.");
        phoneInput.clear();

        String phoneNumber = "+79001234567";
        phoneInput.sendKeys(phoneNumber);

        var agreementLabel = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("label[data-test-id=\"agreement\"]")));
        System.out.println("[TEST] Agreement label found and clickable.");
        agreementLabel.click();

        wait.until(ExpectedConditions.attributeContains(agreementLabel, "class", "checkbox_checked"));
        System.out.println("[TEST] Agreement checkbox marked.");

        var submitButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[.//span[contains(text(), 'Продолжить')]]")));
        System.out.println("[TEST] Submit button found and clickable.");

        if ("true".equals(submitButton.getAttribute("disabled"))) {
            throw new RuntimeException("Кнопка 'Продолжить' заблокирована!");
        }

        submitButton.click();
        System.out.println("[TEST] Clicked submit button.");

        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(90));
        var successBlock = longWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id=\"order-success\"]")));

        assertTrue(successBlock.isDisplayed(), "Блок успеха не появился!");
        System.out.println("[TEST] ✅ Test passed!");
    }
}
