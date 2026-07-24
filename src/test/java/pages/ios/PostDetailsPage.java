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
import utils.StepTimer;

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
        StepTimer.run(
                "Post details | Wait navigation bar",
                () -> wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                postNavigationBar
                        )
                )
        );

        StepTimer.run(
                "Post details | Wait comment field",
                () -> wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                commentField
                        )
                )
        );
    }

    public void addComment(
            String commentText
    ) {
        WebElement field = StepTimer.get(
                "Add comment | Wait field clickable",
                () -> wait.until(
                        ExpectedConditions.elementToBeClickable(
                                commentField
                        )
                )
        );

        StepTimer.run(
                "Add comment | Click field",
                field::click
        );

        StepTimer.run(
                "Add comment | Enter text",
                () -> field.sendKeys(commentText)
        );

        WebElement sendButton = StepTimer.get(
                "Add comment | Wait send clickable",
                () -> fastWait.until(
                        ExpectedConditions.elementToBeClickable(
                                sendCommentButton
                        )
                )
        );

        StepTimer.run(
                "Add comment | Click send",
                sendButton::click
        );

        StepTimer.run(
                "Add comment | Wait comment displayed",
                () -> waitUntilCommentDisplayed(commentText)
        );
    }

    public void waitUntilCommentDisplayed(
            String commentText
    ) {
        fastWait.until(currentDriver ->
                StepTimer.get(
                        "Comment poll | Find created comment",
                        () -> findCommentCellNow(commentText)
                ) != null
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
        WebElement commentCell = StepTimer.get(
                "Delete comment | Wait comment cell",
                () -> fastWait.until(
                        currentDriver ->
                                findCommentCellNow(
                                        commentText
                                )
                )
        );

        StepTimer.run(
                "Delete comment | Swipe cell left",
                () -> swipeCellLeft(commentCell)
        );

        WebElement deleteButton = StepTimer.get(
                "Delete comment | Wait Delete button",
                () -> fastWait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                commentDeleteButton
                        )
                )
        );

        StepTimer.run(
                "Delete comment | Click Delete",
                deleteButton::click
        );

        StepTimer.run(
                "Delete comment | Wait comment disappeared",
                () -> fastWait.until(currentDriver ->
                        StepTimer.get(
                                "Comment poll | Find deleted comment",
                                () -> findCommentCellNow(commentText)
                        ) == null
                )
        );
    }

    public void deletePost() {
        WebElement moreButton = StepTimer.get(
                "Delete post details | Wait More clickable",
                () -> fastWait.until(
                        ExpectedConditions.elementToBeClickable(
                                postMoreButton
                        )
                )
        );

        StepTimer.run(
                "Delete post details | Click More",
                moreButton::click
        );

        WebElement deleteMenuItem = StepTimer.get(
                "Delete post details | Wait Delete menu item",
                () -> fastWait.until(
                        ExpectedConditions.elementToBeClickable(
                                postDeleteMenuItem
                        )
                )
        );

        StepTimer.run(
                "Delete post details | Click Delete menu item",
                deleteMenuItem::click
        );

        StepTimer.run(
                "Delete post details | Confirm alert",
                this::confirmPostDeletion
        );
    }

    private WebElement findCommentCellNow(
            String commentText
    ) {
        try {
            List<WebElement> commentCells = StepTimer.get(
                    "Comment lookup | Find elements",
                    () -> driver.findElements(
                            commentCell(commentText)
                    )
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
        Rectangle bounds = StepTimer.get(
                "Delete comment | Read cell bounds",
                cell::getRect
        );

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

        StepTimer.run(
                "Delete comment | Execute swipe command",
                () -> driver.executeScript(
                        "mobile: dragFromToForDuration",
                        Map.of(
                                "duration", 0.35,
                                "fromX", fromX,
                                "fromY", y,
                                "toX", toX,
                                "toY", y
                        )
                )
        );
    }

    private void confirmPostDeletion() {
        StepTimer.run(
                "Delete post alert | Wait present",
                () -> fastWait.until(
                        ExpectedConditions.alertIsPresent()
                )
        );

        try {
            StepTimer.run(
                    "Delete post alert | Accept native alert",
                    () -> driver.switchTo()
                            .alert()
                            .accept()
            );
            return;

        } catch (WebDriverException ignored) {
            // Fall back to the Appium mobile alert command.
        }

        List<String> buttons = StepTimer.get(
                "Delete post alert | Read buttons",
                this::getAlertButtons
        );

        if (buttons.isEmpty()) {
            throw new IllegalStateException(
                    "Post deletion alert has no buttons."
            );
        }

        String rightButton =
                buttons.get(
                        buttons.size() - 1
                );

        StepTimer.run(
                "Delete post alert | Accept via mobile command",
                () -> driver.executeScript(
                        "mobile: alert",
                        Map.of(
                                "action", "accept",
                                "buttonLabel", rightButton
                        )
                )
        );
    }

    private List<String> getAlertButtons() {
        try {
            Object result = StepTimer.get(
                    "Delete post alert | Execute getButtons",
                    () -> driver.executeScript(
                            "mobile: alert",
                            Map.of(
                                    "action",
                                    "getButtons"
                            )
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
