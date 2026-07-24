package pages.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.time.Duration;
import utils.AndroidConfig;

public class FeedPage extends AndroidBasePage {

    private final By feedTab =
            id("bottom_navigation_feed");

    private final By createPostButton =
            id("sport_feed_create_post");

    private final By likeButton =
            id("iconLike");

    private final By dislikeButton =
            id("iconDislike");

    private final By likesCount =
            id("labelLikesCount");

    private final By dislikesCount =
            id("labelDislikesCount");

    private final By commentsButton =
            id("iconComments");

    public FeedPage(AndroidDriver driver) {
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

    public void waitUntilTopPostReady(String expectedPostText) {
        wait.until(currentDriver -> {
            try {
                List<WebElement> postTexts =
                        currentDriver.findElements(
                                id("bodyText")
                        );

                List<WebElement> likeButtons =
                        currentDriver.findElements(
                                likeButton
                        );

                List<WebElement> dislikeButtons =
                        currentDriver.findElements(
                                dislikeButton
                        );

                List<WebElement> likesCounts =
                        currentDriver.findElements(
                                likesCount
                        );

                List<WebElement> dislikesCounts =
                        currentDriver.findElements(
                                dislikesCount
                        );

                if (postTexts.isEmpty()
                        || likeButtons.isEmpty()
                        || dislikeButtons.isEmpty()
                        || likesCounts.isEmpty()
                        || dislikesCounts.isEmpty()) {

                    return false;
                }

                return expectedPostText.equals(
                        postTexts.get(0).getText()
                )
                        && likeButtons.get(0).isDisplayed()
                        && likeButtons.get(0).isEnabled()
                        && dislikeButtons.get(0).isDisplayed()
                        && dislikeButtons.get(0).isEnabled()
                        && "0".equals(
                        likesCounts.get(0).getText()
                )
                        && "0".equals(
                        dislikesCounts.get(0).getText()
                );

            } catch (StaleElementReferenceException exception) {
                return false;
            }
        });
    }

    public boolean isPostDisplayed(String postText) {
        WebDriverWait shortWait = new WebDriverWait(
                driver,
                Duration.ofSeconds(3)
        );

        try {
            return shortWait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            postText(postText)
                    )
            ).isDisplayed();
        } catch (TimeoutException exception) {
            return false;
        }
    }

    public void waitUntilPostDisappears(String postText) {
        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        postText(postText)
                )
        );
    }

    public void likeTopPost() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        likeButton
                )
        ).click();
    }

    public void dislikeTopPost() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        dislikeButton
                )
        ).click();
    }

    public void waitUntilReactionCounts(
            String expectedLikes,
            String expectedDislikes
    ) {
        wait.until(
                ExpectedConditions.textToBe(
                        likesCount,
                        expectedLikes
                )
        );

        wait.until(
                ExpectedConditions.textToBe(
                        dislikesCount,
                        expectedDislikes
                )
        );
    }

    public String getTopPostLikesCount() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        likesCount
                )
        ).getText();
    }

    public String getTopPostDislikesCount() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        dislikesCount
                )
        ).getText();
    }

    public void openTopPostComments() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        commentsButton
                )
        ).click();
    }

    private By postText(String postText) {
        return AppiumBy.androidUIAutomator(
                "new UiSelector()"
                        + ".resourceId(\""
                        + AndroidConfig.APP_PACKAGE
                        + ":id/bodyText\")"
                        + ".text(\""
                        + postText
                        + "\")"
        );
    }
}
