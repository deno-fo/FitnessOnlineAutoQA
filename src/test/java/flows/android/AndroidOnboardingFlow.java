package flows.android;

import components.android.AndroidSystemDialogHandler;
import components.android.TutorialOverlay;
import io.appium.java_client.android.AndroidDriver;
import pages.android.BodyParametersPage;
import pages.android.GenderSelectionPage;
import pages.android.MainPage;

import java.util.function.BooleanSupplier;

public final class AndroidOnboardingFlow {

    private final GenderSelectionPage
            genderSelectionPage;

    private final BodyParametersPage
            bodyParametersPage;

    private final MainPage mainPage;

    private final TutorialOverlay tutorialOverlay;

    private final AndroidSystemDialogHandler
            systemDialogHandler;

    public AndroidOnboardingFlow(
            AndroidDriver driver
    ) {
        genderSelectionPage =
                new GenderSelectionPage(driver);

        bodyParametersPage =
                new BodyParametersPage(driver);

        mainPage =
                new MainPage(driver);

        tutorialOverlay =
                new TutorialOverlay(driver);

        systemDialogHandler =
                new AndroidSystemDialogHandler(
                        driver
                );
    }

    public void completeGuestWithDefaultUserData() {
        systemDialogHandler.waitUntil(
                genderSelectionPage::isReady
        );

        genderSelectionPage.selectMale();

        systemDialogHandler.waitUntil(
                bodyParametersPage::isReady
        );

        bodyParametersPage
                .continueWithDefaultValues();

        systemDialogHandler.waitUntil(
                mainPage::isReady
        );
    }

    public void completeRegistrationWithDefaultUserData() {
        completeRegistrationWithDefaultUserData(
                mainPage::isReady
        );
    }

    public void completeRegistrationWithDefaultUserData(
            BooleanSupplier expectedNextScreen
    ) {
        systemDialogHandler.waitUntil(
                bodyParametersPage::isReady
        );

        bodyParametersPage
                .continueWithDefaultValues();

        systemDialogHandler.waitUntil(
                expectedNextScreen
        );
    }

    public void completeRegisteredUserLogin() {
        systemDialogHandler.waitUntil(
                mainPage::isReady
        );

        tutorialOverlay.dismissIfPresent();
    }
}