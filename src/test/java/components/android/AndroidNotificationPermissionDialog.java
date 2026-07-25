package components.android;

import pages.android.AndroidBasePage;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;

import java.util.List;

import java.time.Duration;

public class AndroidNotificationPermissionDialog extends AndroidBasePage {
    private final By allowButton =
            AppiumBy.id(
                    "com.android.permissioncontroller:id/permission_allow_button"
            );
    private final By denyButton =
            AppiumBy.id(
                    "com.android.permissioncontroller:id/permission_deny_button"
            );

    public AndroidNotificationPermissionDialog(AndroidDriver driver) {
        super(driver);
    }

    public void allowNotifications() {
        if (!isNotificationPermissionRequired()) {
            return;
        }

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        allowButton
                )
        ).click();
    }

    public void denyNotifications() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        denyButton
                )
        ).click();
    }

    public boolean allowNotificationsIfPresent() {
        if (!isNotificationPermissionRequired()) {
            return false;
        }

        WebDriverWait shortWait =
                new WebDriverWait(
                        driver,
                        Duration.ofMillis(700)
                );

        try {
            shortWait.until(
                    ExpectedConditions.elementToBeClickable(allowButton
                    )
            ).click();

            return true;
        } catch (TimeoutException exception) {
            return false;
        }
    }

    public boolean allowNotificationsIfDisplayed() {
        if (!isNotificationPermissionRequired()) {
            return false;
        }

        List<WebElement> allowButtons =
                driver.findElements(allowButton);

        if (allowButtons.isEmpty()) {
            return false;
        }

        WebElement button = allowButtons.get(0);

        if (!button.isDisplayed()
                || !button.isEnabled()) {
            return false;
        }

        button.click();

        return true;
    }

    private boolean isNotificationPermissionRequired() {
        int apiLevel =
                Integer.parseInt(
                        driver.getCapabilities()
                                .getCapability(
                                        "appium:deviceApiLevel"
                                )
                                .toString()
                );

        return apiLevel >= 33;
    }
}