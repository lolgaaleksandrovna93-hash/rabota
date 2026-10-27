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

import static io.github.bonigarcia.wdm.WebDriverManager.chromedriver;

public class CardOrderValidationTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    static void setupAll() {
        System.out.println("=== CardOrderValidationTest: запуск драйвера ===");
        chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        
        // --- ВАЖНЫЕ ФЛАГИ ДЛЯ CI (GitHub Actions) ---
        options.addArguments("--headless=new");          // Запуск без графического интерфейса (ОБЯЗАТЕЛЬНО для CI)
        options.addArguments("--no-sandbox");             // Отключаем песочницу (требуется для контейнеров)
        options.addArguments("--disable-dev-shm-usage"); // Увеличиваем память для рендеринга
        options.addArguments("--window-size=1920,1080"); // Фиксируем размер окна
        // --------------------------------------------

        driver = new ChromeDriver(options);
        
        // В headless-режиме maximize() может не сработать или быть лишним, 
        // размер уже задан через window-size. Оставляем для совместимости.
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
        System.out.println("1. Открываем страницу...");
        driver.get("http://localhost:9999");

        System.out.println("2. Ищем поле имени (обертку по data-test-id)...");
        // Ищем именно обертку span.input, так как классы ошибки и текста лежат внутри неё
        WebElement nameFieldWrapper = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-test-id='name']")));

        WebElement nameInput = nameFieldWrapper.findElement(By.tagName("input"));

        System.out.println("3. Вводим НЕКОРРЕКТНЫЕ данные (латиница)...");
        nameInput.clear();
        nameInput.sendKeys("Ivanov Ivan");

        System.out.println("4. КЛИК ПО ЧЕКБОКСУ СОГЛАСИЯ...");
        WebElement agreementLabel = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("label[data-test-id='agreement']")));

        agreementLabel.click();
        wait.until(ExpectedConditions.attributeContains(agreementLabel, "class", "checkbox_checked"));
        System.out.println("Галочка поставлена.");

        System.out.println("5. Находим кнопку 'Продолжить'...");
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[contains(text(), 'Продолжить')]]")));

        if ("true".equals(submitButton.getAttribute("disabled"))) {
            throw new RuntimeException("Кнопка 'Продолжить' заблокирована до клика.");
        }

        System.out.println("6. НАЖИМАЕМ КНОПКУ 'Продолжить'...");
        submitButton.click();

        System.out.println("7. Ждем появления ошибки валидации...");

        boolean errorFound = false;

        // СТРАТЕГИЯ 1: Ищем текст ошибки в span.input_sub (САМЫЙ НАДЕЖНЫЙ ВАРИАНТ)
        // На скриншоте видно, что текст лежит именно там.
        try {
            System.out.println("   -> Ищем текст ошибки в блоке .input_sub...");
            WebElement errorTextElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("[data-test-id='name'] .input_sub")));

            String errorText = errorTextElement.getText();
            if (!errorText.isEmpty()) {
                System.out.println("   -> Текст ошибки найден: '" + errorText + "'");
                errorFound = true;
            }
        } catch (Exception e) {
            System.out.println("   -> Блок с текстом ошибки еще не появился или не найден.");
        }

        // СТРАТЕГИЯ 2: Если текста нет, проверяем наличие класса input_invalid у родителя
        if (!errorFound) {
            try {
                System.out.println("   -> Проверяем наличие класса input_invalid у обертки поля...");
                wait.until(ExpectedConditions.attributeContains(nameFieldWrapper, "class", "input_invalid"));
                System.out.println("   -> Класс input_invalid обнаружен у родителя!");
                errorFound = true;
            } catch (Exception e) {
                System.out.println("   -> Класс input_invalid не обнаружен.");
            }
        }

        // ФИНАЛЬНАЯ ПРОВЕРКА
        if (!errorFound) {
            throw new RuntimeException(
                    "❌ НЕ УДАЛОСЬ НАЙТИ ОШИБКУ ВАЛИДАЦИИ!\n" +
                            "   Проверьте в DevTools (F12):\n" +
                            "   1. Появился ли класс 'input_invalid' у элемента [data-test-id='name']?\n" +
                            "   2. Появился ли текст внутри .input_sub?\n" +
                            "   Сейчас DOM не содержит признаков ошибки."
            );
        }

        System.out.println("✅ ТЕСТ ПРОЙДЕН: Ошибка валидации успешно обнаружена и подтверждена.");
    }
}
