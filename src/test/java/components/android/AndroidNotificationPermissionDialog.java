package components.android;
import pages.android.AndroidBasePage;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

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
        int apiLevel = Integer.parseInt(
                driver.getCapabilities()
                        .getCapability("appium:deviceApiLevel")
                        .toString()
        );
        if (apiLevel < 33) {
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
}