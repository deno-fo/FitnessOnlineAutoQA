package pages.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.StepTimer;

import java.util.List;

public class NewPostPage extends IosBasePage {

    private final By title =
            AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' "
                            + "AND name == 'New post'"
            );

    private final By postField =
            AppiumBy.className(
                    "XCUIElementTypeTextView"
            );

    private final By createButton =
            AppiumBy.accessibilityId("CREATE");

    private final By keyboard =
            AppiumBy.className(
                    "XCUIElementTypeKeyboard"
            );

    public NewPostPage(IOSDriver driver) {
        super(driver);
    }

    public void publishTextPost(
            String postText
    ) {
        StepTimer.run(
                "New post form | Wait title",
                () -> wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                title
                        )
                )
        );

        WebElement field = StepTimer.get(
                "New post form | Wait text field",
                () -> wait.until(
                        ExpectedConditions.elementToBeClickable(
                                postField
                        )
                )
        );

        StepTimer.run(
                "New post form | Click text field",
                field::click
        );

        StepTimer.run(
                "New post form | Enter post text",
                () -> field.sendKeys(postText)
        );

        StepTimer.run(
                "New post form | Hide keyboard",
                () -> hideKeyboardIfCovering(createButton)
        );

        WebElement create = StepTimer.get(
                "New post form | Wait CREATE clickable",
                () -> wait.until(
                        ExpectedConditions.elementToBeClickable(
                                createButton
                        )
                )
        );

        StepTimer.run(
                "New post form | Click CREATE",
                create::click
        );
    }

    private void hideKeyboardIfCovering(
            By targetLocator
    ) {
        List<WebElement> keyboards =
                driver.findElements(keyboard);

        if (keyboards.isEmpty()) {
            return;
        }

        WebElement target = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        targetLocator
                )
        );

        Rectangle keyboardBounds =
                keyboards.get(0).getRect();

        Rectangle targetBounds =
                target.getRect();

        int targetBottom =
                targetBounds.getY()
                        + targetBounds.getHeight();

        if (targetBottom
                < keyboardBounds.getY()) {
            return;
        }

        try {
            driver.hideKeyboard();
        } catch (WebDriverException ignored) {
            // Keyboard may already be hidden.
        }
    }
}
