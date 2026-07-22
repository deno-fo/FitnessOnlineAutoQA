package pages.ios;

import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class IosBasePage {

    protected final IOSDriver driver;
    protected final WebDriverWait wait;

    protected IosBasePage(IOSDriver driver) {
        this.driver = driver;

        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );

        this.wait.pollingEvery(
                Duration.ofMillis(250)
        );
    }

    protected boolean isDisplayedNow(By locator) {
        return findVisibleElementNow(locator) != null;
    }

    protected WebElement findVisibleElementNow(
            By locator
    ) {
        try {
            List<WebElement> elements =
                    driver.findElements(locator);

            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed()) {
                        return element;
                    }
                } catch (
                        StaleElementReferenceException ignored
                ) {
                    // UI обновился между поиском
                    // элемента и проверкой видимости.
                }
            }
        } catch (
                StaleElementReferenceException ignored
        ) {
            // Обновилось всё accessibility tree.
        }

        return null;
    }
}