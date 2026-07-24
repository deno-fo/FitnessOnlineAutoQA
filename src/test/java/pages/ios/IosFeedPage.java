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

public class IosFeedPage extends IosBasePage {

    private static final int LIKE_INDEX = 0;
    private static final int DISLIKE_INDEX = 1;
    private static final int COMMENTS_INDEX = 2;

    private final By feedTab =
            AppiumBy.accessibilityId("Feed");

    private final By createPostButton =
            AppiumBy.accessibilityId("Add");

    private final By feedTitle =
            AppiumBy.accessibilityId(
                    "Sport Feed, Actions Menu"
            );

    private final By cells =
            AppiumBy.className(
                    "XCUIElementTypeCell"
            );

    private final By buttons =
            AppiumBy.className(
                    "XCUIElementTypeButton"
            );

    public IosFeedPage(IOSDriver driver) {
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
                        feedTitle
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

    public void waitUntilTopPostReady(
            String expectedPostText
    ) {
        wait.until(currentDriver -> {
            try {
                WebElement topPost =
                        findTopVisibleCell();

                if (topPost == null
                        || !containsVisibleText(
                        topPost,
                        expectedPostText
                )) {
                    return false;
                }

                List<WebElement> actions =
                        findPostActionButtons(topPost);

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

        try {
            return shortWait.until(
                    ExpectedConditions
                            .visibilityOfElementLocated(
                                    postText(postText)
                            )
            ).isDisplayed();

        } catch (TimeoutException exception) {
            return false;
        }
    }

    public void waitUntilPostDisappears(
            String postText
    ) {
        wait.until(
                ExpectedConditions
                        .invisibilityOfElementLocated(
                                postText(postText)
                        )
        );
    }

    public void likeTopPost() {
        waitForPostActionButton(
                LIKE_INDEX
        ).click();
    }

    public void dislikeTopPost() {
        waitForPostActionButton(
                DISLIKE_INDEX
        ).click();
    }

    public void openTopPostComments() {
        waitForPostActionButton(
                COMMENTS_INDEX
        ).click();
    }

    public void waitUntilReactionCounts(
            String expectedLikes,
            String expectedDislikes
    ) {
        wait.until(currentDriver -> {
            List<WebElement> actions =
                    currentTopPostActions();

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
        });
    }

    public String getTopPostLikesCount() {
        return actionCount(
                waitForPostActionButton(
                        LIKE_INDEX
                )
        );
    }

    public String getTopPostDislikesCount() {
        return actionCount(
                waitForPostActionButton(
                        DISLIKE_INDEX
                )
        );
    }

    private WebElement waitForPostActionButton(
            int index
    ) {
        return wait.until(currentDriver -> {
            List<WebElement> actions =
                    currentTopPostActions();

            if (actions.size() <= index) {
                return null;
            }

            WebElement action = actions.get(index);

            return action.isDisplayed()
                    && action.isEnabled()
                    ? action
                    : null;
        });
    }

    private List<WebElement> currentTopPostActions() {
        try {
            WebElement topPost =
                    findTopVisibleCell();

            if (topPost == null) {
                return List.of();
            }

            return findPostActionButtons(topPost);

        } catch (
                StaleElementReferenceException ignored
        ) {
            return List.of();
        }
    }

    private WebElement findTopVisibleCell() {
        List<WebElement> visibleCells =
                new ArrayList<>();

        for (WebElement cell :
                driver.findElements(cells)) {
            try {
                if (cell.isDisplayed()) {
                    visibleCells.add(cell);
                }
            } catch (
                    StaleElementReferenceException ignored
            ) {
                // Accessibility tree is still updating.
            }
        }

        visibleCells.sort(
                Comparator.comparingInt(
                        cell -> cell.getRect().getY()
                )
        );

        return visibleCells.isEmpty()
                ? null
                : visibleCells.get(0);
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
                // Cell was redrawn while actions were read.
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

    private boolean containsVisibleText(
            WebElement cell,
            String text
    ) {
        for (WebElement element :
                cell.findElements(
                        AppiumBy.accessibilityId(text)
                )) {
            try {
                if (element.isDisplayed()) {
                    return true;
                }
            } catch (
                    StaleElementReferenceException ignored
            ) {
                return false;
            }
        }

        return false;
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

    private By postText(String postText) {
        return AppiumBy.accessibilityId(
                postText
        );
    }
}
