package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class IosPostDetailsPage extends IosBasePage {

    private final By postTitle =
            AppiumBy.accessibilityId("Post");

    private final By moreButton =
            AppiumBy.accessibilityId("More");

    private final By deleteMenu =
            AppiumBy.accessibilityId("Delete");

    private final By commentsField =
            AppiumBy.accessibilityId("Add a comment");

    private final By commentButtons =
            AppiumBy.className(
                    "XCUIElementTypeButton"
            );

    public IosPostDetailsPage(IOSDriver driver) {
        super(driver);
    }

    public void waitUntilOpened() {
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        postTitle
                )
        );
    }

    public void openMenu() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        moreButton
                )
        ).click();
    }

    public void deletePost() {
        openMenu();

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        deleteMenu
                )
        ).click();

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        AppiumBy.accessibilityId("Delete")
                )
        ).click();
    }

    public void openComments() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        commentsField
                )
        );
    }
}
