package components.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class IosTutorialOverlay {

    private final IOSDriver driver;

    private final By tutorialPopover =
            AppiumBy.className(
                    "XCUIElementTypePopover"
            );

    public IosTutorialOverlay(IOSDriver driver) {
        this.driver = driver;
    }

    public boolean isDisplayed() {
        return findVisiblePopover() != null;
    }

    public void dismissIfPresent() {
        WebElement popover = findVisiblePopover();

        if (popover == null) {
            return;
        }

        dismiss(popover);
    }

    private void dismiss(WebElement popover) {
        Rectangle bounds = popover.getRect();

        driver.executeScript(
                "mobile: tap",
                Map.of(
                        "x",
                        bounds.getX()
                                + bounds.getWidth() / 2,
                        "y",
                        bounds.getY()
                                + bounds.getHeight() / 2
                )
        );

        WebDriverWait shortWait = new WebDriverWait(
                driver,
                Duration.ofSeconds(5)
        );

        shortWait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        tutorialPopover
                )
        );
    }

    private WebElement findVisiblePopover() {
        List<WebElement> elements =
                driver.findElements(tutorialPopover);

        for (WebElement element : elements) {
            if (element.isDisplayed()) {
                return element;
            }
        }

        return null;
    }
}