package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class GenderSelectionPage
        extends IosBasePage {

    private final By maleOption =
            AppiumBy.accessibilityId(
                    "Male"
            );

    public GenderSelectionPage(
            IOSDriver driver
    ) {
        super(driver);
    }

    public boolean isDisplayedNow() {
        return isDisplayedNow(
                maleOption
        );
    }

    public void selectMale() {
        wait.until(
                ExpectedConditions
                        .elementToBeClickable(
                                maleOption
                        )
        ).click();
    }
}