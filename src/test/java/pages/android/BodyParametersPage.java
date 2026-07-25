package pages.android;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class BodyParametersPage extends AndroidBasePage {

    private final By continueButton =
            id("button");

    public BodyParametersPage(AndroidDriver driver) {
        super(driver);
    }

    public void continueWithDefaultValues() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        continueButton
                )
        ).click();
    }

    public boolean isReady() {
        return isReadyWithoutWait(
                continueButton
        );
    }
}