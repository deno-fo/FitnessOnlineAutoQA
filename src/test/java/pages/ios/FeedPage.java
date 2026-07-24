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

    private String cachedPostText;
    private PostActions cachedPostActions;

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
                Duration.ofSeconds(2)
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
        cachedPostActions = wait.until(
                currentDriver ->
                        findPostActionsNow(postText)
        );

        cachedPostText = postText;
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

        clearCachedActions(postText);
    }

    public void likePost(String postText) {
        clickPostAction(
                postText,
                LIKE_INDEX
        );
    }

    public void dislikePost(String postText) {
        clickPostAction(
                postText,
                DISLIKE_INDEX
        );
    }

    public void openPostComments(String postText) {
        clickPostAction(
                postText,
                COMMENTS_INDEX
        );
    }

    public ReactionCounts waitUntilReactionCounts(
            String postText,
            String expectedLikes,
            String expectedDislikes
    ) {
        return fastWait.until(currentDriver -> {
            try {
                PostActions actions =
                        getCachedActions(postText);

                String likes =
                        actionCount(actions.like());

                String dislikes =
                        actionCount(actions.dislike());

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
                refreshCachedActions(postText);
                return null;
            }
        });
    }

    private void clickPostAction(
            String postText,
            int index
    ) {
        try {
            actionByIndex(
                    getCachedActions(postText),
                    index
            ).click();

        } catch (StaleElementReferenceException ignored) {
            refreshCachedActions(postText);

            actionByIndex(
                    getCachedActions(postText),
                    index
            ).click();
        }
    }

    private PostActions getCachedActions(
            String postText
    ) {
        if (postText.equals(cachedPostText)
                && cachedPostActions != null) {
            return cachedPostActions;
        }

        refreshCachedActions(postText);
        return cachedPostActions;
    }

    private void refreshCachedActions(
            String postText
    ) {
        cachedPostActions = fastWait.until(
                currentDriver ->
                        findPostActionsNow(postText)
        );

        cachedPostText = postText;
    }

    private void clearCachedActions(
            String postText
    ) {
        if (!postText.equals(cachedPostText)) {
            return;
        }

        cachedPostText = null;
        cachedPostActions = null;
    }

    private WebElement actionByIndex(
            PostActions actions,
            int index
    ) {
        return switch (index) {
            case LIKE_INDEX -> actions.like();
            case DISLIKE_INDEX -> actions.dislike();
            case COMMENTS_INDEX -> actions.comments();
            default -> throw new IllegalArgumentException(
                    "Unsupported feed action index: "
                            + index
            );
        };
    }

    private PostActions findPostActionsNow(
            String postText
    ) {
        WebElement postCell =
                findPostCellNow(postText);

        if (postCell == null) {
            return null;
        }

        List<WebElement> actions =
                findPostActionButtons(postCell);

        if (actions.size() < 3) {
            return null;
        }

        return new PostActions(
                actions.get(LIKE_INDEX),
                actions.get(DISLIKE_INDEX),
                actions.get(COMMENTS_INDEX)
        );
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
                return List.of();
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

    private record PostActions(
            WebElement like,
            WebElement dislike,
            WebElement comments
    ) {
    }

    public record ReactionCounts(
            String likes,
            String dislikes
    ) {
    }
}
