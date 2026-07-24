package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class IosNewPostPage extends IosBasePage {

    private final By title =
            AppiumBy.accessibilityId("New post");

    private final By postField =
            AppiumBy.className(
                    "XCUIElementTypeTextView"
            );

    private final By createButton =
            AppiumBy.accessibilityId("CREATE");

    public IosNewPostPage(IOSDriver driver) {
        super(driver);
    }

    public void publishTextPost(String postText) {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        title
                )
        );

        WebElement input = wait.until(
                ExpectedConditions.elementToBeClickable(
                        postField
                )
        );

        input.click();
        input.sendKeys(postText);

        try {
            wait.until(
                    ExpectedConditions.elementToBeClickable(
                            createButton
                    )
            ).click();

        } catch (WebDriverException exception) {
            try {
                driver.hideKeyboard();
            } catch (WebDriverException ignored) {
                // Keyboard may already be hidden.
            }

            wait.until(
                    ExpectedConditions.elementToBeClickable(
                            createButton
                    )
            ).click();
        }
    }
}
