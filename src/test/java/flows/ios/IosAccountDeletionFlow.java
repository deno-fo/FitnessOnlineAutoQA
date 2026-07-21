package flows.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.ios.MainPage;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.Dimension;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class IosAccountDeletionFlow {

    private final IOSDriver driver;
    private final WebDriverWait wait;
    private final MainPage mainPage;

    private final By settingsItem =
            AppiumBy.accessibilityId("Settings");

    private final By deleteAccountItem =
            AppiumBy.accessibilityId(
                    "Delete account"
            );

    private final By firstDeleteButton =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeButton' "
                            + "AND name == 'DELETE'"
            );

    public IosAccountDeletionFlow(
            IOSDriver driver
    ) {
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );
        this.mainPage = new MainPage(driver);
    }

    private void tapFirstDeleteButton() {
        WebElement deleteButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        firstDeleteButton
                )
        );

        Rectangle bounds =
                deleteButton.getRect();

        driver.executeScript(
                "mobile: tap",
                Map.of(
                        "x",
                        bounds.getX()
                                + bounds.getWidth() / 2,
                        "y",
                        bounds.getY()
                                + bounds.getHeight() / 2
                )
        );
    }

    private WebElement scrollToDeleteAccount() {
        int maxScrolls = 5;

        for (int scroll = 0;
             scroll < maxScrolls;
             scroll++) {

            List<WebElement> elements =
                    driver.findElements(
                            deleteAccountItem
                    );

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
                "Delete account item was not found "
                        + "after scrolling."
        );
    }

    private void confirmFinalDeletion() {
        WebDriverWait alertWait = new WebDriverWait(
                driver,
                Duration.ofSeconds(5)
        );

        alertWait.until(
                ExpectedConditions.alertIsPresent()
        );

        Dimension screenSize =
                driver.manage()
                        .window()
                        .getSize();

        driver.executeScript(
                "mobile: tap",
                Map.of(
                        "x",
                        screenSize.getWidth() / 3,
                        "y",
                        (int) (
                                screenSize.getHeight()
                                        * 0.60
                        )
                )
        );
    }

    public boolean deleteAccountIfPossible() {
        if (!mainPage.openMoreIfAvailable()) {
            return false;
        }

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        settingsItem
                )
        ).click();

        WebElement deleteAccount =
                scrollToDeleteAccount();

        deleteAccount.click();

        tapFirstDeleteButton();

        confirmFinalDeletion();

        return true;
    }
}