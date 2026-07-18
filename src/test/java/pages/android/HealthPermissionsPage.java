package pages.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class HealthPermissionsPage {

    private final WebDriverWait wait;
    private final By allowAllSwitch =
            AppiumBy.id("android:id/switch_widget");
    private final By allowButton =
            AppiumBy.id(
                    "com.android.healthconnect.controller:id/primary_button_outline"
            );
    private final By denyAccessButton =
            AppiumBy.id(
                    "com.android.healthconnect.controller:id/secondary_button"
            );

    public HealthPermissionsPage(AndroidDriver driver) {
        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );
    }

    public void denyAccess() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        denyAccessButton
                )
        ).click();
    }

    public void enableAllowAll() {
        WebElement allowAll = wait.until(
                ExpectedConditions.elementToBeClickable(
                        allowAllSwitch
                )
        );
        boolean isChecked = Boolean.parseBoolean(
                allowAll.getAttribute("checked")
        );
        if (!isChecked) {
            allowAll.click();
        }
        wait.until(
                ExpectedConditions.attributeToBe(
                        allowAllSwitch,
                        "checked",
                        "true"
                )
        );
    }

    public void confirmHealthPermissions() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        allowButton
                )
        ).click();
    }
}