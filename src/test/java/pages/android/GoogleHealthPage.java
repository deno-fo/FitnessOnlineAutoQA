package pages.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class GoogleHealthPage extends AndroidBasePage {

    private final By grantAccessButton =
            id("grand_permissions_button");
    private final By skipButton =
            id("skip_button");
    private final By persuasionGrantButton =
            AppiumBy.id("android:id/button1");
    private final By persuasionSkipButton =
            AppiumBy.id("android:id/button3");

    public GoogleHealthPage(AndroidDriver driver) {
        super(driver);
    }

    public void grantAccess() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        grantAccessButton
                )
        ).click();
    }

    public void skipAccess() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        skipButton
                )
        ).click();
    }

    public void acceptPersuasion() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        persuasionGrantButton
                )
        ).click();
    }

    public void declinePersuasion() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        persuasionSkipButton
                )
        ).click();
    }

    public void continueWithoutAccess() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        skipButton
                )
        ).click();
    }
}