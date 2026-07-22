package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class BodyParametersPage
        extends IosBasePage {

    private final By continueButton =
            AppiumBy.accessibilityId(
                    "CONTINUE"
            );

    public BodyParametersPage(
            IOSDriver driver
    ) {
        super(driver);
    }

    public boolean isDisplayedNow() {
        return isDisplayedNow(
                continueButton
        );
    }

    public void continueWithDefaultValues() {
        wait.until(
                ExpectedConditions
                        .elementToBeClickable(
                                continueButton
                        )
        ).click();
    }
}