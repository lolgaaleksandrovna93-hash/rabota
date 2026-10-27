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

import static io.github.bonigarcia.wdm.WebDriverManager.chromedriver;

public class CardOrderTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    static void setup() {
        System.out.println("=== Запуск драйвера ===");
        chromedriver().setup();

        ChromeOptions options = new ChromeOptions()
                .addArguments("--disable-dev-shm-usage")
                .addArguments("--no-sandbox")
                .addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @AfterAll
    static void teardown() {
        if (driver != null) {
            System.out.println("=== Закрытие браузера ===");
            driver.quit();
        }
    }

    @Test
    void shouldSubmitCardOrderFormSuccessfully() {
        System.out.println("1. Открываем страницу...");
        driver.get("http://localhost:9999");

        // Шаг 2: Вводим имя
        System.out.println("Вводим ФИО...");
        var nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id=\"name\"] input")));
        nameInput.clear();
        nameInput.sendKeys("Иванов Иван Иванович");

        // Шаг 3: Вводим телефон (посимвольно для корректной работы маски)
        System.out.println("Вводим номер телефона...");
        var phoneInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id=\"phone\"] input")));
        phoneInput.clear();

        String phoneNumber = "+79001234567";
        for (char c : phoneNumber.toCharArray()) {
            phoneInput.sendKeys(String.valueOf(c));
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }

        // Шаг 4: Нажимаем на чекбокс согласия
        System.out.println("Кликаем на блок согласия (label)...");
        var agreementLabel = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("label[data-test-id=\"agreement\"]")));
        agreementLabel.click();

        // Проверка: убеждаемся, что класс checkbox_checked появился (галочка стоит)
        System.out.println("Проверяем, что галочка установлена...");
        wait.until(ExpectedConditions.attributeContains(agreementLabel, "class", "checkbox_checked"));

        // Шаг 5: НАЖИМАЕМ КНОПКУ "ПРОДОЛЖИТЬ"
        System.out.println("Находим кнопку 'Продолжить'...");

        // Ищем кнопку по тексту "Продолжить" внутри неё. Это самый стабильный способ,
        // так как классы (button_view_extra, button_size_m) могут меняться при сборке проекта.
        var submitButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[.//span[contains(text(), 'Продолжить')]]")));

        // ВАЖНО: Проверяем, не заблокирована ли кнопка (атрибут disabled)
        // Если кнопка заблокирована, значит, поля заполнены неверно (ошибка валидации).
        if ("true".equals(submitButton.getAttribute("disabled"))) {
            throw new RuntimeException("Кнопка 'Продолжить' заблокирована! Проверьте корректность заполнения полей (телефон, имя) или наличие галочки.");
        }

        System.out.println("Нажимаем кнопку 'Продолжить'...");
        submitButton.click();

        // Шаг 6: Ждем появления блока успеха
        System.out.println("Ожидаем появления блока успеха (до 60 сек)...");
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(60));

        // Ищем блок успеха по data-test-id (стабильный селектор)
        var successBlock = longWait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test-id=\"order-success\"]")));

        assertTrue(successBlock.isDisplayed(), "Блок успеха не появился!");
        System.out.println("✅ ТЕСТ ПРОЙДЕН: заявка успешно отправлена!");
    }
}
