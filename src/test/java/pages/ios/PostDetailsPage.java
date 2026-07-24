package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PostDetailsPage extends IosBasePage {

    private final By postNavigationBar =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeNavigationBar' "
                            + "AND name == 'Post'"
            );

    private final By postMoreButton =
            AppiumBy.accessibilityId(
                    "icon more info vertical"
            );

    private final By commentField =
            AppiumBy.className(
                    "XCUIElementTypeTextView"
            );

    private final By sendCommentButton =
            AppiumBy.accessibilityId("send");

    private final By cells =
            AppiumBy.className(
                    "XCUIElementTypeCell"
            );

    private final By commentDeleteButton =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeButton' "
                            + "AND name == 'Delete'"
            );

    private final By postDeleteMenuItem =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' "
                            + "AND name == 'Delete'"
            );

    public PostDetailsPage(IOSDriver driver) {
        super(driver);
    }

    public void waitUntilOpened() {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        postNavigationBar
                )
        );

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        commentField
                )
        );
    }

    public void addComment(
            String commentText
    ) {
        WebElement field = wait.until(
                ExpectedConditions.elementToBeClickable(
                        commentField
                )
        );

        field.click();
        field.sendKeys(commentText);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        sendCommentButton
                )
        ).click();

        waitUntilCommentDisplayed(commentText);
    }

    public void waitUntilCommentDisplayed(
            String commentText
    ) {
        wait.until(currentDriver ->
                findVisibleCommentCell(commentText)
                        != null
        );
    }

    public boolean isCommentDisplayed(
            String commentText
    ) {
        WebDriverWait shortWait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(3)
                );

        shortWait.pollingEvery(
                Duration.ofMillis(250)
        );

        try {
            return shortWait.until(
                    currentDriver ->
                            findVisibleCommentCell(
                                    commentText
                            ) != null
            );

        } catch (TimeoutException ignored) {
            return false;
        }
    }

    public void deleteComment(
            String commentText
    ) {
        WebElement commentCell = wait.until(
                currentDriver ->
                        findVisibleCommentCell(
                                commentText
                        )
        );

        swipeCellLeft(commentCell);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        commentDeleteButton
                )
        ).click();

        wait.until(currentDriver ->
                findVisibleCommentCell(commentText)
                        == null
        );
    }

    public void deletePost() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        postMoreButton
                )
        ).click();

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        postDeleteMenuItem
                )
        ).click();

        confirmPostDeletion();
    }

    private WebElement findVisibleCommentCell(
            String commentText
    ) {
        By textLocator =
                AppiumBy.accessibilityId(commentText);

        for (WebElement cell :
                driver.findElements(cells)) {
            try {
                if (!cell.isDisplayed()) {
                    continue;
                }

                for (WebElement textElement :
                        cell.findElements(textLocator)) {
                    if (textElement.isDisplayed()) {
                        return cell;
                    }
                }

            } catch (
                    StaleElementReferenceException ignored
            ) {
                // The comments table was refreshed.
            }
        }

        return null;
    }

    private void swipeCellLeft(
            WebElement cell
    ) {
        Rectangle bounds = cell.getRect();

        int fromX =
                bounds.getX()
                        + (int) (
                        bounds.getWidth() * 0.80
                );

        int toX =
                bounds.getX()
                        + (int) (
                        bounds.getWidth() * 0.25
                );

        int y =
                bounds.getY()
                        + bounds.getHeight() / 2;

        driver.executeScript(
                "mobile: dragFromToForDuration",
                Map.of(
                        "duration", 0.35,
                        "fromX", fromX,
                        "fromY", y,
                        "toX", toX,
                        "toY", y
                )
        );
    }

    private void confirmPostDeletion() {
        wait.until(
                ExpectedConditions.alertIsPresent()
        );

        List<String> buttons =
                getAlertButtons();

        if (!buttons.isEmpty()) {
            String rightButton =
                    buttons.get(
                            buttons.size() - 1
                    );

            try {
                driver.executeScript(
                        "mobile: alert",
                        Map.of(
                                "action", "accept",
                                "buttonLabel", rightButton
                        )
                );

                return;

            } catch (WebDriverException ignored) {
                // Fall back to Selenium Alert API.
            }
        }

        driver.switchTo()
                .alert()
                .accept();
    }

    private List<String> getAlertButtons() {
        try {
            Object result =
                    driver.executeScript(
                            "mobile: alert",
                            Map.of(
                                    "action",
                                    "getButtons"
                            )
                    );

            if (!(result
                    instanceof Collection<?> rawButtons)) {
                return List.of();
            }

            List<String> buttons =
                    new ArrayList<>();

            for (Object rawButton : rawButtons) {
                if (rawButton != null) {
                    buttons.add(
                            String.valueOf(rawButton)
                    );
                }
            }

            return buttons;

        } catch (WebDriverException ignored) {
            return List.of();
        }
    }
}
