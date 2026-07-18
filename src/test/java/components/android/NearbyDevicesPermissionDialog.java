package components.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pages.android.AndroidBasePage;

public class NearbyDevicesPermissionDialog extends AndroidBasePage {

    private final By allowButton =
            AppiumBy.id(
                    "com.android.permissioncontroller:id/permission_allow_button"
            );

    private final By denyButton =
            AppiumBy.id(
                    "com.android.permissioncontroller:id/permission_deny_button"
            );

    public NearbyDevicesPermissionDialog(AndroidDriver driver) {
        super(driver);
    }

    public void allowNearbyDevices() {
        int apiLevel = Integer.parseInt(
                driver.getCapabilities()
                        .getCapability("appium:deviceApiLevel")
                        .toString()
        );

        if (apiLevel < 31) {
            return;
        }

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        allowButton
                )
        ).click();
    }

    public void denyNearbyDevices() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        denyButton
                )
        ).click();
    }
}