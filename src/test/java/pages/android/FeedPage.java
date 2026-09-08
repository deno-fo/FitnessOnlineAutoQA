package pages.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.AndroidConfig;

import java.time.Duration;

public class FeedPage extends AndroidBasePage {

    private final By feedTab = id("bottom_navigation_feed");
    private final By createPostButton = id("sport_feed_create_post");

    public FeedPage(AndroidDriver driver) {
        super(driver);
    }

    public void openFeed() {
        wait.until(ExpectedConditions.elementToBeClickable(feedTab)).click();
        waitUntilReady();
    }

    public void waitUntilReady() {
        wait.until(ExpectedConditions.elementToBeClickable(createPostButton));
    }

    public void openCreatePost() {
        wait.until(ExpectedConditions.elementToBeClickable(createPostButton)).click();
    }

    public void waitUntilPostReady(String postText) {
        wait.until(currentDriver ->
                isReadyWithoutWait(postControl(postText, "iconLike"))
                        && isReadyWithoutWait(postControl(postText, "iconDislike"))
                        && isReadyWithoutWait(postControl(postText, "iconComments"))
        );
        waitUntilReactionCounts(postText, "0", "0");
    }

    public boolean isPostDisplayed(String postText) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
        try {
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(
                    postText(postText)
            )).isDisplayed();
        } catch (TimeoutException exception) {
            return false;
        }
    }

    public void waitUntilPostDisappears(String postText) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(postText(postText)));
    }

    public void likePost(String postText) {
        clickPostControl(postText, "iconLike");
    }

    public void dislikePost(String postText) {
        clickPostControl(postText, "iconDislike");
    }

    public void openPostComments(String postText) {
        clickPostControl(postText, "iconComments");
    }

    public void waitUntilReactionCounts(
            String postText, String expectedLikes, String expectedDislikes
    ) {
        By likes = postControl(postText, "labelLikesCount");
        By dislikes = postControl(postText, "labelDislikesCount");
        wait.until(currentDriver -> {
            try {
                var likeElements = currentDriver.findElements(likes);
                var dislikeElements = currentDriver.findElements(dislikes);
                return likeElements.size() == 1 && dislikeElements.size() == 1
                        && expectedLikes.equals(likeElements.get(0).getText())
                        && expectedDislikes.equals(dislikeElements.get(0).getText());
            } catch (StaleElementReferenceException exception) {
                return false;
            }
        });
    }

    public String getPostLikesCount(String postText) {
        return readPostControl(postText, "labelLikesCount");
    }

    public String getPostDislikesCount(String postText) {
        return readPostControl(postText, "labelDislikesCount");
    }

    private void clickPostControl(String postText, String resourceId) {
        wait.until(ExpectedConditions.elementToBeClickable(
                postControl(postText, resourceId)
        )).click();
    }

    private String readPostControl(String postText, String resourceId) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                postControl(postText, resourceId)
        )).getText();
    }

    private By postControl(String postText, String resourceId) {
        return AppiumBy.xpath(postControlXPath(postText, resourceId));
    }

    // The nearest ancestor with this body and action row must contain only one post.
    // Otherwise a missing control could incorrectly resolve to a neighbouring post.
    static String postControlXPath(String postText, String resourceId) {
        String prefix = AndroidConfig.APP_PACKAGE + ":id/";
        String body = "@resource-id=" + xpathLiteral(prefix + "bodyText");
        return "//*[" + body + " and @text=" + xpathLiteral(postText) + "]"
                + "/ancestor::*[count(.//*[" + body + "])=1"
                + " and .//*[@resource-id=" + xpathLiteral(prefix + "iconLike") + "]"
                + " and .//*[@resource-id=" + xpathLiteral(prefix + "iconDislike") + "]"
                + " and .//*[@resource-id=" + xpathLiteral(prefix + "iconComments") + "]"
                + "][1]//*[@resource-id=" + xpathLiteral(prefix + resourceId) + "]";
    }

    private By postText(String postText) {
        return AppiumBy.xpath("//*[@resource-id="
                + xpathLiteral(AndroidConfig.APP_PACKAGE + ":id/bodyText")
                + " and @text=" + xpathLiteral(postText) + "]");
    }

    private static String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        return "concat('" + value.replace("'", "',\"'\",'") + "')";
    }
}
