package components.android;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.BooleanSupplier;

public final class AndroidSystemDialogHandler {

    private static final Duration TRANSITION_TIMEOUT =
            Duration.ofSeconds(20);

    private static final Duration POLLING_INTERVAL =
            Duration.ofMillis(200);

    private final AndroidDriver driver;

    private final AndroidNotificationPermissionDialog
            notificationPermissionDialog;

    public AndroidSystemDialogHandler(
            AndroidDriver driver
    ) {
        this.driver = driver;

        notificationPermissionDialog =
                new AndroidNotificationPermissionDialog(
                        driver
                );
    }

    public void waitUntil(
            BooleanSupplier expectedScreenReady
    ) {
        WebDriverWait transitionWait =
                new WebDriverWait(
                        driver,
                        TRANSITION_TIMEOUT
                );

        transitionWait.pollingEvery(
                POLLING_INTERVAL
        );

        transitionWait.ignoring(
                StaleElementReferenceException.class
        );

        transitionWait.until(ignored -> {
            boolean dialogHandled =
                    notificationPermissionDialog
                            .allowNotificationsIfDisplayed();

            if (dialogHandled) {
                return false;
            }

            return expectedScreenReady
                    .getAsBoolean();
        });
    }

    public void handleVisibleDialogs() {
        notificationPermissionDialog
                .allowNotificationsIfDisplayed();
    }
}
