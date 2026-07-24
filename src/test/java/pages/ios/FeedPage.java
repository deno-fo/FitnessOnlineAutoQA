package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FeedPage extends IosBasePage {

    private static final int LIKE_INDEX = 0;
    private static final int DISLIKE_INDEX = 1;
    private static final int COMMENTS_INDEX = 2;

    private final By feedTab =
            AppiumBy.accessibilityId("Feed");

    private final By feedNavigationBar =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeNavigationBar' "
                            + "AND name == "
                            + "'Sport Feed, Actions Menu'"
            );

    private final By createPostButton =
            AppiumBy.accessibilityId("Add");

    private final By postCells =
            AppiumBy.className(
                    "XCUIElementTypeCell"
            );

    private final By buttons =
            AppiumBy.className(
                    "XCUIElementTypeButton"
            );

    public FeedPage(IOSDriver driver) {
        super(driver);
    }

    public void openFeed() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        feedTab
                )
        ).click();

        waitUntilReady();
    }

    public void waitUntilReady() {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        feedNavigationBar
                )
        );

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        createPostButton
                )
        );
    }

    public void openCreatePost() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        createPostButton
                )
        ).click();
    }

    public void waitUntilPostReady(
            String postText
    ) {
        wait.until(currentDriver -> {
            try {
                WebElement postCell =
                        findVisiblePostCell(postText);

                if (postCell == null) {
                    return false;
                }

                List<WebElement> actions =
                        findPostActionButtons(postCell);

                if (actions.size() < 3) {
                    return false;
                }

                return actions.get(LIKE_INDEX)
                        .isEnabled()
                        && actions.get(DISLIKE_INDEX)
                        .isEnabled()
                        && actions.get(COMMENTS_INDEX)
                        .isEnabled()
                        && "0".equals(
                        actionCount(
                                actions.get(LIKE_INDEX)
                        )
                )
                        && "0".equals(
                        actionCount(
                                actions.get(DISLIKE_INDEX)
                        )
                )
                        && "0".equals(
                        actionCount(
                                actions.get(COMMENTS_INDEX)
                        )
                );

            } catch (
                    StaleElementReferenceException ignored
            ) {
                return false;
            }
        });
    }

    public boolean isPostDisplayed(
            String postText
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
                            findVisiblePostCell(postText)
                                    != null
            );

        } catch (TimeoutException ignored) {
            return false;
        }
    }

    public void waitUntilPostDisappears(
            String postText
    ) {
        wait.until(currentDriver ->
                findVisiblePostCell(postText) == null
        );
    }

    public void likePost(String postText) {
        waitForPostActionButton(
                postText,
                LIKE_INDEX
        ).click();
    }

    public void dislikePost(String postText) {
        waitForPostActionButton(
                postText,
                DISLIKE_INDEX
        ).click();
    }

    public void openPostComments(String postText) {
        waitForPostActionButton(
                postText,
                COMMENTS_INDEX
        ).click();
    }

    public void waitUntilReactionCounts(
            String postText,
            String expectedLikes,
            String expectedDislikes
    ) {
        wait.until(currentDriver -> {
            try {
                List<WebElement> actions =
                        currentPostActions(postText);

                return actions.size() >= 3
                        && expectedLikes.equals(
                        actionCount(
                                actions.get(LIKE_INDEX)
                        )
                )
                        && expectedDislikes.equals(
                        actionCount(
                                actions.get(DISLIKE_INDEX)
                        )
                );

            } catch (
                    StaleElementReferenceException ignored
            ) {
                return false;
            }
        });
    }

    public String getPostLikesCount(
            String postText
    ) {
        return actionCount(
                waitForPostActionButton(
                        postText,
                        LIKE_INDEX
                )
        );
    }

    public String getPostDislikesCount(
            String postText
    ) {
        return actionCount(
                waitForPostActionButton(
                        postText,
                        DISLIKE_INDEX
                )
        );
    }

    private WebElement waitForPostActionButton(
            String postText,
            int index
    ) {
        return wait.until(currentDriver -> {
            try {
                List<WebElement> actions =
                        currentPostActions(postText);

                if (actions.size() <= index) {
                    return null;
                }

                WebElement action = actions.get(index);

                return action.isDisplayed()
                        && action.isEnabled()
                        ? action
                        : null;

            } catch (
                    StaleElementReferenceException ignored
            ) {
                return null;
            }
        });
    }

    private List<WebElement> currentPostActions(
            String postText
    ) {
        WebElement postCell =
                findVisiblePostCell(postText);

        if (postCell == null) {
            return List.of();
        }

        return findPostActionButtons(postCell);
    }

    private WebElement findVisiblePostCell(
            String postText
    ) {
        By textLocator =
                AppiumBy.accessibilityId(postText);

        for (WebElement cell :
                driver.findElements(postCells)) {
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
                // The feed was redrawn during polling.
            }
        }

        return null;
    }

    private List<WebElement> findPostActionButtons(
            WebElement postCell
    ) {
        Rectangle cellBounds =
                postCell.getRect();

        int actionRowTop =
                cellBounds.getY()
                        + cellBounds.getHeight()
                        - 70;

        List<WebElement> actions =
                new ArrayList<>();

        for (WebElement button :
                postCell.findElements(buttons)) {
            try {
                Rectangle bounds =
                        button.getRect();

                if (button.isDisplayed()
                        && bounds.getY() >= actionRowTop
                        && bounds.getX() < 220) {

                    actions.add(button);
                }

            } catch (
                    StaleElementReferenceException ignored
            ) {
                // The post cell was refreshed.
            }
        }

        actions.sort(
                Comparator.comparingInt(
                        button -> button
                                .getRect()
                                .getX()
                )
        );

        return actions;
    }

    private String actionCount(
            WebElement action
    ) {
        String name =
                action.getAttribute("name");

        return name == null
                ? action.getText()
                : name;
    }
}
