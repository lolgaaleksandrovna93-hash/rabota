import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

// УДАЛИТЕ ЭТУ ИМПОРТНУЮ СТРОКУ:
// import static io.github.bonigarcia.wdm.WebDriverManager.chromedriver;

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

        // ВАЖНО: Убрали chromedriver().setup()
        driver = new ChromeDriver(options);
        
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
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
        // ... (весь остальной код теста остается БЕЗ ИЗМЕНЕНИЙ) ...
        System.out.println("1. Открываем страницу...");
        driver.get("http://localhost:9999");
        
        WebElement nameFieldWrapper = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-test-id='name']")));

        WebElement nameInput = nameFieldWrapper.findElement(By.tagName("input"));

        System.out.println("3. Вводим НЕКОРРЕКТНЫЕ данные...");
        nameInput.clear();
        nameInput.sendKeys("Ivanov Ivan");

        WebElement agreementLabel = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("label[data-test-id='agreement']")));

        agreementLabel.click();
        wait.until(ExpectedConditions.attributeContains(agreementLabel, "class", "checkbox_checked"));

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[contains(text(), 'Продолжить')]]")));

        if ("true".equals(submitButton.getAttribute("disabled"))) {
            throw new RuntimeException("Кнопка 'Продолжить' заблокирована до клика.");
        }

        submitButton.click();

        System.out.println("7. Ждем появления ошибки валидации...");

        boolean errorFound = false;

        try {
            WebElement errorTextElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("[data-test-id='name'] .input_sub")));

            String errorText = errorTextElement.getText();
            if (!errorText.isEmpty()) {
                System.out.println("   -> Текст ошибки найден: '" + errorText + "'");
                errorFound = true;
            }
        } catch (Exception e) {
            System.out.println("   -> Блок с текстом ошибки не найден.");
        }

        if (!errorFound) {
            try {
                wait.until(ExpectedConditions.attributeContains(nameFieldWrapper, "class", "input_invalid"));
                System.out.println("   -> Класс input_invalid обнаружен!");
                errorFound = true;
            } catch (Exception e) {
                System.out.println("   -> Класс input_invalid не обнаружен.");
            }
        }

        if (!errorFound) {
            throw new RuntimeException("❌ НЕ УДАЛОСЬ НАЙТИ ОШИБКУ ВАЛИДАЦИИ!");
        }

        System.out.println("✅ ТЕСТ ПРОЙДЕН!");
    }
}
