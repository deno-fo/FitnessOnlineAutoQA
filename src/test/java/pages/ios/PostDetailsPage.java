package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
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

    private final WebDriverWait fastWait;

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

        fastWait = new WebDriverWait(
                driver,
                Duration.ofSeconds(3)
        );

        fastWait.pollingEvery(
                Duration.ofMillis(100)
        );
    }

    public void waitUntilOpened() {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        postNavigationBar
                )
        );

        wait.until(
                ExpectedConditions.presenceOfElementLocated(
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

        fastWait.until(
                ExpectedConditions.elementToBeClickable(
                        sendCommentButton
                )
        ).click();

        waitUntilCommentDisplayed(commentText);
    }

    public void waitUntilCommentDisplayed(
            String commentText
    ) {
        fastWait.until(currentDriver ->
                findCommentCellNow(commentText)
                        != null
        );
    }

    public boolean isCommentDisplayedNow(
            String commentText
    ) {
        return findCommentCellNow(commentText)
                != null;
    }

    public void deleteComment(
            String commentText
    ) {
        WebElement commentCell = fastWait.until(
                currentDriver ->
                        findCommentCellNow(
                                commentText
                        )
        );

        swipeCellLeft(commentCell);

        fastWait.until(
                ExpectedConditions.presenceOfElementLocated(
                        commentDeleteButton
                )
        ).click();

        fastWait.until(currentDriver ->
                findCommentCellNow(commentText)
                        == null
        );
    }

    public void deletePost() {
        fastWait.until(
                ExpectedConditions.elementToBeClickable(
                        postMoreButton
                )
        ).click();

        fastWait.until(
                ExpectedConditions.elementToBeClickable(
                        postDeleteMenuItem
                )
        ).click();

        confirmPostDeletion();
    }

    private WebElement findCommentCellNow(
            String commentText
    ) {
        try {
            List<WebElement> commentCells =
                    driver.findElements(
                            commentCell(commentText)
                    );

            return commentCells.isEmpty()
                    ? null
                    : commentCells.get(0);

        } catch (
                StaleElementReferenceException ignored
        ) {
            return null;
        }
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
        fastWait.until(
                ExpectedConditions.alertIsPresent()
        );

        try {
            driver.switchTo()
                    .alert()
                    .accept();
            return;

        } catch (WebDriverException ignored) {
            // Fall back to the Appium mobile alert command.
        }

        List<String> buttons =
                getAlertButtons();

        if (buttons.isEmpty()) {
            throw new IllegalStateException(
                    "Post deletion alert has no buttons."
            );
        }

        String rightButton =
                buttons.get(
                        buttons.size() - 1
                );

        driver.executeScript(
                "mobile: alert",
                Map.of(
                        "action", "accept",
                        "buttonLabel", rightButton
                )
        );
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

    private By commentCell(
            String commentText
    ) {
        return AppiumBy.xpath(
                "//XCUIElementTypeCell"
                        + "[.//*[@name='"
                        + commentText
                        + "']]"
        );
    }
}
