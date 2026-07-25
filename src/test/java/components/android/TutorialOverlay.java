package components.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.android.AndroidBasePage;

import java.time.Duration;
import java.util.Map;

public class TutorialOverlay extends AndroidBasePage {

    private final By tutorialCard =
            AppiumBy.id("fitness.online.app:id/card_view");

    public TutorialOverlay(AndroidDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayedWithoutWait(
                tutorialCard
        );
    }

    public void dismiss() {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        tutorialCard
                )
        );

        closeVisibleOverlay();
    }

    public void dismissIfPresent() {
        WebDriverWait optionalWait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );

        try {
            optionalWait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            tutorialCard
                    )
            );
        } catch (TimeoutException exception) {
            return;
        }

        closeVisibleOverlay();
    }

    private void closeVisibleOverlay() {
        Dimension screenSize =
                driver.manage().window().getSize();

        int tapX = screenSize.width / 2;
        int tapY = screenSize.height * 2 / 3;

        for (int attempt = 1; attempt <= 3; attempt++) {
            driver.executeScript(
                    "mobile: clickGesture",
                    Map.of(
                            "x", tapX,
                            "y", tapY
                    )
            );

            if (waitUntilCardDisappears()) {
                return;
            }
        }

        throw new IllegalStateException(
                "Tutorial overlay did not close after 3 tap attempts."
        );
    }

    private boolean waitUntilCardDisappears() {
        WebDriverWait shortWait = new WebDriverWait(
                driver,
                Duration.ofMillis(700)
        );

        try {
            return shortWait.until(
                    ExpectedConditions.invisibilityOfElementLocated(
                            tutorialCard
                    )
            );
        } catch (TimeoutException exception) {
            return false;
        }
    }
}
