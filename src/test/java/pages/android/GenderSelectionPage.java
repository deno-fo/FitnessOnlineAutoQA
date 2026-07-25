package pages.android;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class GenderSelectionPage extends AndroidBasePage {

    private final By maleBanner =
            id("imgMale");
    private final By femaleBanner =
            id("imgFemale");

    public GenderSelectionPage(AndroidDriver driver) {
        super(driver);
    }

    public void selectMale() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        maleBanner
                )
        ).click();
    }

    public void selectFemale() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        femaleBanner
                )
        ).click();
    }

    public boolean isMaleSelectionReady() {
        return driver.findElements(maleBanner)
                .stream()
                .anyMatch(
                        element ->
                                element.isDisplayed()
                                        && element.isEnabled()
                );
    }

    public boolean isReady() {
        return isReadyWithoutWait(
                maleBanner
        );
    }
}