package components.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class IosTutorialOverlay {

    private final IOSDriver driver;

    /*
     * Никакого isVisible == 1 внутри predicate.
     * Находим popover по типу, а реальную
     * видимость проверяем отдельно.
     */
    private final By tutorialPopover =
            AppiumBy.className(
                    "XCUIElementTypePopover"
            );

    public IosTutorialOverlay(
            IOSDriver driver
    ) {
        this.driver = driver;
    }

    public boolean isDisplayed() {
        return findVisiblePopover() != null;
    }

    public void waitAndDismissIfPresent(
            Duration timeout
    ) {
        WebDriverWait appearanceWait =
                new WebDriverWait(
                        driver,
                        timeout
                );

        appearanceWait.pollingEvery(
                Duration.ofMillis(200)
        );

        try {
            appearanceWait.until(
                    currentDriver ->
                            findVisiblePopover() != null
            );
        } catch (TimeoutException ignored) {
            return;
        }

        dismissIfPresent();
    }

    public boolean dismissVisiblePopoverFast() {
        WebElement popover =
                findVisiblePopover();

        if (popover == null) {
            return false;
        }

        try {
            tapCenter(popover);
            return true;

        } catch (
                StaleElementReferenceException ignored
        ) {
            WebElement refreshedPopover =
                    findVisiblePopover();

            if (refreshedPopover == null) {
                return false;
            }

            tapCenter(refreshedPopover);
            return true;
        }
    }

    public void dismissIfPresent() {
        WebElement popover =
                findVisiblePopover();

        if (popover == null) {
            return;
        }

        try {
            tapCenter(popover);
        } catch (
                StaleElementReferenceException ignored
        ) {
            /*
             * Подсказка могла пересоздаться
             * между поиском и получением bounds.
             */
            WebElement refreshedPopover =
                    findVisiblePopover();

            if (refreshedPopover == null) {
                return;
            }

            tapCenter(refreshedPopover);
        }

        WebDriverWait disappearanceWait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(2)
                );

        disappearanceWait.pollingEvery(
                Duration.ofMillis(200)
        );

        try {
            disappearanceWait.until(
                    currentDriver ->
                            findVisiblePopover() == null
            );
        } catch (TimeoutException exception) {
            throw new TimeoutException(
                    "iOS tutorial popover remained visible "
                            + "after tapping its center.",
                    exception
            );
        }
    }

    private void tapCenter(
            WebElement element
    ) {
        Rectangle bounds =
                element.getRect();

        int x =
                bounds.getX()
                        + bounds.getWidth() / 2;

        int y =
                bounds.getY()
                        + bounds.getHeight() / 2;

        driver.executeScript(
                "mobile: tap",
                Map.of(
                        "x", x,
                        "y", y
                )
        );
    }

    private WebElement findVisiblePopover() {
        try {
            List<WebElement> popovers =
                    driver.findElements(
                            tutorialPopover
                    );

            for (WebElement popover : popovers) {
                try {
                    if (popover.isDisplayed()) {
                        return popover;
                    }
                } catch (
                        StaleElementReferenceException ignored
                ) {
                    /*
                     * Конкретный popover устарел.
                     * Проверяем остальные элементы.
                     */
                }
            }
        } catch (
                StaleElementReferenceException ignored
        ) {
            /*
             * Accessibility tree обновилось
             * целиком во время поиска.
             */
        }

        return null;
    }
}