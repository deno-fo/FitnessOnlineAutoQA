package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
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

    private final WebDriverWait fastWait;

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

    private final By numericButtons =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeButton' "
                            + "AND name MATCHES '^[0-9]+$'"
            );

    public FeedPage(IOSDriver driver) {
        super(driver);

        fastWait = new WebDriverWait(
                driver,
                Duration.ofSeconds(3)
        );

        fastWait.pollingEvery(
                Duration.ofMillis(100)
        );
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
                        findPostCellNow(postText);

                return postCell != null
                        && findPostActionButtons(postCell)
                        .size() >= 3;

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
        return findPostCellNow(postText)
                != null;
    }

    public void waitUntilPostDisappears(
            String postText
    ) {
        fastWait.until(currentDriver ->
                findPostCellNow(postText) == null
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

    public ReactionCounts waitUntilReactionCounts(
            String postText,
            String expectedLikes,
            String expectedDislikes
    ) {
        return fastWait.until(currentDriver -> {
            try {
                List<WebElement> actions =
                        currentPostActions(postText);

                if (actions.size() < 3) {
                    return null;
                }

                String likes =
                        actionCount(
                                actions.get(LIKE_INDEX)
                        );

                String dislikes =
                        actionCount(
                                actions.get(DISLIKE_INDEX)
                        );

                if (!expectedLikes.equals(likes)
                        || !expectedDislikes.equals(
                        dislikes
                )) {
                    return null;
                }

                return new ReactionCounts(
                        likes,
                        dislikes
                );

            } catch (
                    StaleElementReferenceException ignored
            ) {
                return null;
            }
        });
    }

    private WebElement waitForPostActionButton(
            String postText,
            int index
    ) {
        return fastWait.until(currentDriver -> {
            try {
                List<WebElement> actions =
                        currentPostActions(postText);

                return actions.size() > index
                        ? actions.get(index)
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
                findPostCellNow(postText);

        return postCell == null
                ? List.of()
                : findPostActionButtons(postCell);
    }

    private WebElement findPostCellNow(
            String postText
    ) {
        List<WebElement> cells =
                driver.findElements(
                        postCell(postText)
                );

        return cells.isEmpty()
                ? null
                : cells.get(0);
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
                postCell.findElements(numericButtons)) {
            try {
                Rectangle bounds =
                        button.getRect();

                if (bounds.getY() >= actionRowTop
                        && bounds.getX() < 220) {
                    actions.add(button);
                }

            } catch (
                    StaleElementReferenceException ignored
            ) {
                // The post cell was redrawn.
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

    private By postCell(String postText) {
        return AppiumBy.xpath(
                "//XCUIElementTypeCell"
                        + "[.//XCUIElementTypeButton"
                        + "[@name='"
                        + postText
                        + "']]"
        );
    }

    public record ReactionCounts(
            String likes,
            String dislikes
    ) {
    }
}
