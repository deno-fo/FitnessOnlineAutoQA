package tests.android;

import org.junit.jupiter.api.Test;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import components.android.AndroidNotificationPermissionDialog;
import flows.AccountDeletionFlow;
import flows.CustomWorkoutCreationFlow;
import flows.GoogleHealthAccessFlow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import pages.android.BodyParametersPage;
import pages.android.EmailRegistrationPage;
import pages.android.ExerciseSettingsPage;
import pages.android.LoginPage;
import pages.android.WorkoutDayDetailsPage;
import pages.android.WorkoutDayEditorPage;
import pages.android.WorkoutDaysPage;

public class AndroidWorkoutDayEditAndDeleteTest
        extends BaseAndroidTest {

    private LoginPage loginPage;
    private EmailRegistrationPage emailRegistrationPage;
    private GoogleHealthAccessFlow googleHealthAccessFlow;
    private BodyParametersPage bodyParametersPage;
    private AndroidNotificationPermissionDialog
            notificationPermissionDialog;

    private CustomWorkoutCreationFlow
            customWorkoutCreationFlow;

    private WorkoutDaysPage workoutDaysPage;
    private WorkoutDayDetailsPage workoutDayDetailsPage;
    private WorkoutDayEditorPage workoutDayEditorPage;
    private ExerciseSettingsPage exerciseSettingsPage;

    private AccountDeletionFlow accountDeletionFlow;

    @BeforeEach
    public void createPages() {
        loginPage =
                new LoginPage(driver);
        emailRegistrationPage =
                new EmailRegistrationPage(driver);
        googleHealthAccessFlow =
                new GoogleHealthAccessFlow(driver);
        bodyParametersPage =
                new BodyParametersPage(driver);
        notificationPermissionDialog =
                new AndroidNotificationPermissionDialog(driver);
        customWorkoutCreationFlow =
                new CustomWorkoutCreationFlow(driver);
        workoutDaysPage =
                new WorkoutDaysPage(driver);
        workoutDayDetailsPage =
                new WorkoutDayDetailsPage(driver);
        workoutDayEditorPage =
                new WorkoutDayEditorPage(driver);
        exerciseSettingsPage =
                new ExerciseSettingsPage(driver);
        accountDeletionFlow =
                new AccountDeletionFlow(driver);
    }

    @Test
    public void shouldEditAndDeleteWorkoutDay()
            throws InterruptedException {

        registerNewUserWithGoogleHealth();

        customWorkoutCreationFlow.createWorkoutWithOneExercise(
                TestData.CUSTOM_WORKOUT_NAME,
                TestData.CUSTOM_WORKOUT_DAY_NAME,
                TestData.EXERCISE_NAME,
                TestData.EXERCISE_SETS,
                TestData.EXERCISE_REPEATS,
                TestData.EXERCISE_WEIGHT
        );

        workoutDaysPage.openWorkoutDay(
                TestData.CUSTOM_WORKOUT_DAY_NAME
        );

        workoutDayDetailsPage.openWorkoutDayEditor();

        workoutDayEditorPage.enterWorkoutDayName(
                TestData.UPDATED_WORKOUT_DAY_NAME
        );

        workoutDayEditorPage.openExerciseSettings(
                TestData.EXERCISE_NAME
        );

        exerciseSettingsPage.configureExercise(
                TestData.UPDATED_EXERCISE_SETS,
                TestData.UPDATED_EXERCISE_REPEATS,
                TestData.UPDATED_EXERCISE_WEIGHT
        );

        workoutDayEditorPage.saveWorkoutDay();

        assertEquals(
                TestData.UPDATED_WORKOUT_DAY_NAME,
                workoutDayDetailsPage.getWorkoutDayTitle(),
                "Workout day name was not updated."
        );

        String recommendations =
                workoutDayDetailsPage.getExerciseRecommendations();

        assertTrue(
                recommendations.contains(
                        TestData.UPDATED_EXERCISE_SETS
                ),
                "Updated sets are not displayed."
        );

        assertTrue(
                recommendations.contains(
                        TestData.UPDATED_EXERCISE_REPEATS
                ),
                "Updated repeats are not displayed."
        );

        assertTrue(
                recommendations.contains(
                        TestData.UPDATED_EXERCISE_WEIGHT
                ),
                "Updated weight is not displayed."
        );

        workoutDayDetailsPage.openWorkoutDayEditor();
        workoutDayEditorPage.deleteWorkoutDay();

        assertTrue(
                workoutDaysPage.isWorkoutDayAbsent(
                        TestData.UPDATED_WORKOUT_DAY_NAME
                ),
                "Workout day was not deleted."
        );

        assertTrue(
                workoutDaysPage.isEmptyWorkoutDaysPlaceholderDisplayed(),
                "Empty workout days placeholder was not displayed."
        );
    }

    private void registerNewUserWithGoogleHealth() {
        loginPage.skipWelcomeScreen();
        loginPage.openEmailAuthentication();
        emailRegistrationPage.registerMaleUser(
                TestData.uniqueEmail(),
                TestData.PASSWORD,
                TestData.NAME,
                TestData.SURNAME
        );
        googleHealthAccessFlow.continueDependingOnAvailability(
                healthConnectAvailable
        );
        bodyParametersPage.continueWithDefaultValues();
        notificationPermissionDialog.allowNotifications();
    }

    @AfterEach
    public void cleanUpCreatedAccount()
            throws InterruptedException {

        if (accountDeletionFlow != null
                && accountDeletionFlow.canDeleteAccount()) {
            accountDeletionFlow.deleteAccount();
        }
    }
}