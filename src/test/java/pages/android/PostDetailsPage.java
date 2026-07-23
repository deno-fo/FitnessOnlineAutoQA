package pages.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.AndroidConfig;

import java.time.Duration;

public class PostDetailsPage extends AndroidBasePage {

    private final By messageField =
            id("message");

    private final By sendCommentButton =
            AppiumBy.accessibilityId("Send");

    private final By commentMoreButton =
            id("edit");

    private final By postMoreButton =
            id("post_more");

    private final By bottomSheet =
            id("design_bottom_sheet");

    private final By deleteMenuItem =
            AppiumBy.androidUIAutomator(
                    "new UiSelector()"
                            + ".resourceId(\""
                            + AndroidConfig.APP_PACKAGE
                            + ":id/title\")"
                            + ".text(\"Delete\")"
            );

    private final By confirmDeleteButton =
            AppiumBy.id("android:id/button1");

    public PostDetailsPage(AndroidDriver driver) {
        super(driver);
    }

    public void waitUntilOpened() {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        messageField
                )
        );
    }

    public void addComment(String commentText) {
        WebElement messageInput = wait.until(
                ExpectedConditions.elementToBeClickable(
                        messageField
                )
        );

        messageInput.click();
        messageInput.sendKeys(commentText);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        sendCommentButton
                )
        ).click();

        waitUntilCommentDisplayed(commentText);
    }

    public void waitUntilCommentDisplayed(String commentText) {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        commentText(commentText)
                )
        );
    }

    public boolean isCommentDisplayed(String commentText) {
        WebDriverWait shortWait = new WebDriverWait(
                driver,
                Duration.ofSeconds(3)
        );

        try {
            return shortWait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            commentText(commentText)
                    )
            ).isDisplayed();
        } catch (TimeoutException exception) {
            return false;
        }
    }

    public void deleteComment(String commentText) {
        waitUntilCommentDisplayed(commentText);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        commentMoreButton
                )
        ).click();

        deleteFromBottomSheet();

        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        commentText(commentText)
                )
        );
    }

    public void deletePost() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        postMoreButton
                )
        ).click();

        deleteFromBottomSheet();

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        confirmDeleteButton
                )
        ).click();
    }

    private void deleteFromBottomSheet() {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        bottomSheet
                )
        );

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        deleteMenuItem
                )
        ).click();
    }

    private By commentText(String commentText) {
        return AppiumBy.androidUIAutomator(
                "new UiSelector()"
                        + ".resourceId(\""
                        + AndroidConfig.APP_PACKAGE
                        + ":id/bodyText\")"
                        + ".text(\""
                        + commentText
                        + "\")"
        );
    }
}
