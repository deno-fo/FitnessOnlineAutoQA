package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.StepTimer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class FeedPage extends IosBasePage {

    private static final int LIKE_INDEX = 0;
    private static final int DISLIKE_INDEX = 1;
    private static final int COMMENTS_INDEX = 2;

    private final WebDriverWait actionWait;
    private final WebDriverWait reactionWait;

    private String cachedPostText;
    private PostActionPoints cachedPostActionPoints;

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

        actionWait = new WebDriverWait(
                driver,
                Duration.ofSeconds(2)
        );

        actionWait.pollingEvery(
                Duration.ofMillis(100)
        );

        reactionWait = new WebDriverWait(
                driver,
                Duration.ofSeconds(3)
        );

        reactionWait.pollingEvery(
                Duration.ofMillis(400)
        );
    }

    public void tapFeedTab() {
        WebElement tab = StepTimer.get(
                "Feed tab | Wait clickable",
                () -> wait.until(
                        ExpectedConditions.elementToBeClickable(
                                feedTab
                        )
                )
        );

        StepTimer.run(
                "Feed tab | Click",
                tab::click
        );
    }

    public void openFeed() {
        tapFeedTab();
        waitUntilReady();
    }

    public void waitUntilReady() {
        StepTimer.run(
                "Feed ready | Wait navigation bar",
                () -> wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                feedNavigationBar
                        )
                )
        );

        StepTimer.run(
                "Feed ready | Wait Add button",
                () -> wait.until(
                        ExpectedConditions.elementToBeClickable(
                                createPostButton
                        )
                )
        );
    }

    public void openCreatePost() {
        WebElement button = StepTimer.get(
                "Create form | Wait Add clickable",
                () -> wait.until(
                        ExpectedConditions.elementToBeClickable(
                                createPostButton
                        )
                )
        );

        StepTimer.run(
                "Create form | Click Add",
                button::click
        );
    }

    public void waitUntilPostReady(
            String postText
    ) {
        PostActionSnapshot snapshot = wait.until(
                currentDriver ->
                        findPostActionSnapshotNow(postText)
        );

        cachedPostText = postText;
        cachedPostActionPoints = snapshot.points();
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
        actionWait.until(currentDriver ->
                findPostCellNow(postText) == null
        );

        clearCachedActionPoints(postText);
    }

    public void likePost(String postText) {
        tapPostAction(
                postText,
                LIKE_INDEX
        );
    }

    public void dislikePost(String postText) {
        tapPostAction(
                postText,
                DISLIKE_INDEX
        );
    }

    public void openPostComments(String postText) {
        tapPostAction(
                postText,
                COMMENTS_INDEX
        );
    }

    public ReactionCounts waitUntilReactionCounts(
            String postText,
            String expectedLikes,
            String expectedDislikes
    ) {
        return reactionWait.until(currentDriver -> {
            try {
                PostActionSnapshot snapshot =
                        findPostActionSnapshotNow(
                                postText
                        );

                if (snapshot == null) {
                    return null;
                }

                String likes = actionCount(
                        snapshot.elements().like()
                );

                String dislikes = actionCount(
                        snapshot.elements().dislike()
                );

                if (!expectedLikes.equals(likes)
                        || !expectedDislikes.equals(
                        dislikes
                )) {
                    return null;
                }

                cachedPostText = postText;
                cachedPostActionPoints =
                        snapshot.points();

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

    private void tapPostAction(
            String postText,
            int index
    ) {
        ActionPoint point = StepTimer.get(
                "Feed action | Resolve coordinates",
                () -> actionPointByIndex(
                        getCachedActionPoints(postText),
                        index
                )
        );

        StepTimer.run(
                "Feed action | Execute coordinate tap",
                () -> driver.executeScript(
                        "mobile: tap",
                        Map.of(
                                "x", point.x(),
                                "y", point.y()
                        )
                )
        );
    }

    private PostActionPoints getCachedActionPoints(
            String postText
    ) {
        if (postText.equals(cachedPostText)
                && cachedPostActionPoints != null) {
            return cachedPostActionPoints;
        }

        PostActionSnapshot snapshot = actionWait.until(
                currentDriver ->
                        findPostActionSnapshotNow(postText)
        );

        cachedPostText = postText;
        cachedPostActionPoints = snapshot.points();

        return cachedPostActionPoints;
    }

    private void clearCachedActionPoints(
            String postText
    ) {
        if (!postText.equals(cachedPostText)) {
            return;
        }

        cachedPostText = null;
        cachedPostActionPoints = null;
    }

    private ActionPoint actionPointByIndex(
            PostActionPoints points,
            int index
    ) {
        return switch (index) {
            case LIKE_INDEX -> points.like();
            case DISLIKE_INDEX -> points.dislike();
            case COMMENTS_INDEX -> points.comments();
            default -> throw new IllegalArgumentException(
                    "Unsupported feed action index: "
                            + index
            );
        };
    }

    private PostActionSnapshot findPostActionSnapshotNow(
            String postText
    ) {
        WebElement postCell =
                findPostCellNow(postText);

        if (postCell == null) {
            return null;
        }

        List<LocatedAction> actions =
                findPostActions(postCell);

        if (actions.size() < 3) {
            return null;
        }

        LocatedAction like =
                actions.get(LIKE_INDEX);

        LocatedAction dislike =
                actions.get(DISLIKE_INDEX);

        LocatedAction comments =
                actions.get(COMMENTS_INDEX);

        return new PostActionSnapshot(
                new PostActionElements(
                        like.element(),
                        dislike.element(),
                        comments.element()
                ),
                new PostActionPoints(
                        centerOf(like.bounds()),
                        centerOf(dislike.bounds()),
                        centerOf(comments.bounds())
                )
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

    private List<LocatedAction> findPostActions(
            WebElement postCell
    ) {
        Rectangle cellBounds =
                postCell.getRect();

        int actionRowTop =
                cellBounds.getY()
                        + cellBounds.getHeight()
                        - 70;

        List<LocatedAction> actions =
                new ArrayList<>();

        for (WebElement button :
                postCell.findElements(numericButtons)) {
            try {
                Rectangle bounds =
                        button.getRect();

                if (bounds.getY() >= actionRowTop
                        && bounds.getX() < 220) {
                    actions.add(
                            new LocatedAction(
                                    button,
                                    bounds
                            )
                    );
                }

            } catch (
                    StaleElementReferenceException ignored
            ) {
                return List.of();
            }
        }

        actions.sort(
                Comparator.comparingInt(
                        action -> action
                                .bounds()
                                .getX()
                )
        );

        return actions;
    }

    private ActionPoint centerOf(
            Rectangle bounds
    ) {
        return new ActionPoint(
                bounds.getX()
                        + bounds.getWidth() / 2,
                bounds.getY()
                        + bounds.getHeight() / 2
        );
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

    private record LocatedAction(
            WebElement element,
            Rectangle bounds
    ) {
    }

    private record ActionPoint(
            int x,
            int y
    ) {
    }

    private record PostActionElements(
            WebElement like,
            WebElement dislike,
            WebElement comments
    ) {
    }

    private record PostActionPoints(
            ActionPoint like,
            ActionPoint dislike,
            ActionPoint comments
    ) {
    }

    private record PostActionSnapshot(
            PostActionElements elements,
            PostActionPoints points
    ) {
    }

    public record ReactionCounts(
            String likes,
            String dislikes
    ) {
    }
}
