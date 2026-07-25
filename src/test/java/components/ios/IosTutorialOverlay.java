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

    private final By tutorialMarker =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeImage' "
                            + "AND ("
                            + "name == 'tooltip_icon_info' "
                            + "OR name == 'tooltip_icon_warning1'"
                            + ")"
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
            /*
             * Сначала ищем один из уникальных
             * значков подсказки приложения.
             *
             * Это не позволяет WDA по 30 секунд
             * искать отсутствующий Popover
             * во всём accessibility tree.
             */
            if (driver.findElements(
                    tutorialMarker
            ).isEmpty()) {
                return null;
            }

            List<WebElement> popovers =
                    driver.findElements(
                            tutorialPopover
                    );

            if (popovers.isEmpty()) {
                return null;
            }

            /*
             * У SwiftUI значение displayed
             * для Popover бывает ложным.
             * Наличие уникального значка уже
             * подтверждает открытую подсказку.
             */
            return popovers.get(0);

        } catch (
                StaleElementReferenceException ignored
        ) {
            return null;
        }
    }
}