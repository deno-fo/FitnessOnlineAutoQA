package flows;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.android.AndroidBasePage;

public class LogoutFlow extends AndroidBasePage {
    private final By moreTab =
            AppiumBy.id(
                    "fitness.online.app:id/bottom_navigation_more"
            );
    private final By logoutItem =
            AppiumBy.androidUIAutomator(
                    "new UiSelector()" +
                            ".resourceId(\"fitness.online.app:id/settingsItemText\")" +
                            ".text(\"Log out\")"
            );
    private final By confirmLogoutButton =
            AppiumBy.id("android:id/button1");
    private final By emailAuthenticationButton =
            AppiumBy.id("fitness.online.app:id/byEmail");
    public LogoutFlow(AndroidDriver driver) {
        super(driver);
    }

    public void logOut() throws InterruptedException {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        moreTab
                )
        ).click();

        scrollDownToLogout();

        Thread.sleep(500);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        logoutItem
                )
        ).click();

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        confirmLogoutButton
                )
        ).click();

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        emailAuthenticationButton
                )
        );
    }

    private void scrollDownToLogout() {
        int maxScrolls = 5;

        for (int scroll = 0; scroll < maxScrolls; scroll++) {
            if (!driver.findElements(logoutItem).isEmpty()) {
                return;
            }

            driver.findElement(
                    AppiumBy.androidUIAutomator(
                            "new UiScrollable(" +
                                    "new UiSelector().scrollable(true)" +
                                    ").scrollForward()"
                    )
            );
        }

        if (driver.findElements(logoutItem).isEmpty()) {
            throw new IllegalStateException(
                    "Log out item was not found after scrolling down."
            );
        }
    }
}