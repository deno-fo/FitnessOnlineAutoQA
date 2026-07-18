package flows;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.android.AndroidBasePage;

import java.time.Duration;

public class AccountDeletionFlow extends AndroidBasePage {
    private final By moreTab =
            AppiumBy.id(
                    "fitness.online.app:id/bottom_navigation_more"
            );
    private final By settingsItem =
            AppiumBy.androidUIAutomator(
                    "new UiSelector()" +
                            ".resourceId(\"fitness.online.app:id/settingsItemText\")" +
                            ".text(\"Settings\")"
            );
    private final By confirmDeleteButton =
            AppiumBy.id("android:id/button1");
    private final By loginSkipButton =
            AppiumBy.id("fitness.online.app:id/skip");

    public AccountDeletionFlow(AndroidDriver driver) {
        super(driver);
    }

    public boolean canDeleteAccount() {
        WebDriverWait shortWait = new WebDriverWait(
                driver,
                Duration.ofSeconds(3)
        );

        try {
            return shortWait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            moreTab
                    )
            ).isDisplayed();
        } catch (TimeoutException exception) {
            return false;
        }
    }

    public void deleteAccount()
            throws InterruptedException {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        moreTab
                )
        ).click();

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        settingsItem
                )
        ).click();

        By deleteAccountItem =
                AppiumBy.androidUIAutomator(
                        "new UiSelector()" +
                                ".resourceId(\"fitness.online.app:id/settingsItemText\")" +
                                ".text(\"Delete account\")"
                );
        int maxScrolls = 5;

        for (int scroll = 0; scroll < maxScrolls; scroll++) {
            if (!driver.findElements(deleteAccountItem).isEmpty()) {
                break;
            }

            driver.findElement(
                    AppiumBy.androidUIAutomator(
                            "new UiScrollable(new UiSelector().scrollable(true))" +
                                    ".scrollForward()"
                    )
            );
        }

        if (driver.findElements(deleteAccountItem).isEmpty()) {
            throw new IllegalStateException(
                    "Delete account item was not found after scrolling down."
            );
        }

        Thread.sleep(500);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        deleteAccountItem
                )
        ).click();

        WebElement firstConfirmButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        confirmDeleteButton
                )
        );

        firstConfirmButton.click();

        wait.until(
                ExpectedConditions.stalenessOf(
                        firstConfirmButton
                )
        );

        WebElement secondConfirmButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        confirmDeleteButton
                )
        );

        Thread.sleep(500);

        secondConfirmButton.click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        loginSkipButton
                )
        );
    }
}