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
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @AfterAll
    static void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void shouldSubmitCardOrderFormSuccessfully() {
        driver.get("http://localhost:9999");

        var nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id=\"name\"] input")));
        nameInput.clear();
        nameInput.sendKeys("Иванов Иван Иванович");

        var phoneInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id=\"phone\"] input")));
        phoneInput.clear();

        String phoneNumber = "+79001234567";
        for (char c : phoneNumber.toCharArray()) {
            phoneInput.sendKeys(String.valueOf(c));
        }

        var agreementLabel = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("label[data-test-id=\"agreement\"]")));
        agreementLabel.click();
        wait.until(ExpectedConditions.attributeContains(agreementLabel, "class", "checkbox_checked"));

        var submitButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[.//span[contains(text(), 'Продолжить')]]")));

        if ("true".equals(submitButton.getAttribute("disabled"))) {
            throw new RuntimeException("Кнопка 'Продолжить' заблокирована!");
        }

        submitButton.click();

        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(60));
        var successBlock = longWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id=\"order-success\"]")));

        assertTrue(successBlock.isDisplayed(), "Блок успеха не появился!");
    }
}

