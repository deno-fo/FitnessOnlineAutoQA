package pages.android;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class NewPostPage extends AndroidBasePage {

    private final By postField =
            id("post");

    private final By confirmPostButton =
            id("new_post_confirm");

    public NewPostPage(AndroidDriver driver) {
        super(driver);
    }

    public void publishTextPost(String postText) {
        WebElement postInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        postField
                )
        );

        postInput.sendKeys(postText);

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        confirmPostButton
                )
        ).click();

        wait.until(
                ExpectedConditions.stalenessOf(
                        postInput
                )
        );
    }
}
