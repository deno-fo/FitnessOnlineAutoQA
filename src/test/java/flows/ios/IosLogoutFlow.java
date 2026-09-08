package flows.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.ios.MainPage;
import pages.ios.LoginPage;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class IosLogoutFlow {

    private final IOSDriver driver;
    private final WebDriverWait wait;
    private final MainPage mainPage;

    private final By logoutItem =
            AppiumBy.accessibilityId("Log out");

    private final By confirmLogoutButton =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeButton' "
                            + "AND name == 'Logout'"
            );

    public IosLogoutFlow(IOSDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );
        this.mainPage = new MainPage(driver);
    }

    public void logOut() {
        mainPage.openMore();

        WebElement logout = scrollToLogout();
        logout.click();

        confirmLogout();
    }

    public void logOutAfterInterruptedScenario() {
        if (new IosCleanupRecovery(driver).openAccountMenu()) {
            scrollToLogout().click();
            confirmLogout();
        }
        new LoginPage(driver).waitUntilSignedOut();
    }

    private WebElement scrollToLogout() {
        int maxScrolls = 5;

        for (int scroll = 0; scroll < maxScrolls; scroll++) {
            List<WebElement> elements =
                    driver.findElements(logoutItem);

            for (WebElement element : elements) {
                if (element.isDisplayed()) {
                    return element;
                }
            }

            driver.executeScript(
                    "mobile: swipe",
                    Map.of("direction", "up")
            );
        }

        throw new IllegalStateException(
                "Log out item was not found after scrolling."
        );
    }

    private void confirmLogout() {
        WebDriverWait alertWait = new WebDriverWait(
                driver,
                Duration.ofSeconds(5)
        );

        try {
            Alert alert = alertWait.until(
                    ExpectedConditions.alertIsPresent()
            );

            alert.accept();
            return;
        } catch (WebDriverException ignored) {
            // Используем кнопку как запасной вариант.
        }

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        confirmLogoutButton
                )
        ).click();
    }
}
