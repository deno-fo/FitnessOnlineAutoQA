package tests;

import components.android.AndroidNotificationPermissionDialog;
import flows.AccountDeletionFlow;
import flows.CustomWorkoutCreationFlow;
import flows.GoogleHealthAccessFlow;
import flows.WorkoutExecutionFlow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.android.BodyParametersPage;
import pages.android.EmailRegistrationPage;
import pages.android.LoginPage;
import pages.android.WorkoutDaysPage;
import pages.android.WorkoutReportPage;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AndroidCustomWorkoutTest extends BaseAndroidTest {

    private LoginPage loginPage;
    private EmailRegistrationPage emailRegistrationPage;
    private GoogleHealthAccessFlow googleHealthAccessFlow;
    private BodyParametersPage bodyParametersPage;
    private AndroidNotificationPermissionDialog notificationPermissionDialog;

    private CustomWorkoutCreationFlow customWorkoutCreationFlow;
    private WorkoutDaysPage workoutDaysPage;
    private WorkoutExecutionFlow workoutExecutionFlow;
    private WorkoutReportPage workoutReportPage;

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

        workoutExecutionFlow =
                new WorkoutExecutionFlow(driver);

        workoutReportPage =
                new WorkoutReportPage(driver);

        accountDeletionFlow =
                new AccountDeletionFlow(driver);
    }

    @Test
    public void shouldCreateAndCompleteCustomWorkout()
            throws InterruptedException {

        registerNewUser();

        customWorkoutCreationFlow.createWorkoutWithOneExercise(
                TestData.CUSTOM_WORKOUT_NAME,
                TestData.CUSTOM_WORKOUT_DAY_NAME,
                TestData.EXERCISE_NAME,
                TestData.EXERCISE_SETS,
                TestData.EXERCISE_REPEATS,
                TestData.EXERCISE_WEIGHT
        );

        assertTrue(
                workoutDaysPage.isWorkoutDayDisplayed(
                        TestData.CUSTOM_WORKOUT_DAY_NAME
                ),
                "Workout day was not created."
        );

        workoutExecutionFlow.completeWorkout(
                TestData.CUSTOM_WORKOUT_DAY_NAME,
                TestData.EXERCISE_WEIGHT,
                TestData.EXERCISE_REPEATS,
                Integer.parseInt(TestData.EXERCISE_SETS)
        );

        assertTrue(
                workoutDaysPage.isWorkoutDayDisplayed(
                        TestData.CUSTOM_WORKOUT_DAY_NAME
                ),
                "Workout day was not created."
        );

        assertEquals(
                "100%",
                workoutReportPage.getCompletionPercentage(),
                "Workout completion percentage is wrong."
        );

        assertTrue(
                workoutReportPage
                        .getCompletedRepeatsText()
                        .contains("20 reps"),
                "Report does not contain 20 completed reps."
        );

        assertTrue(
                workoutReportPage
                        .getLiftedWeightText()
                        .contains("800 kg"),
                "Report does not contain 800 kg of lifted weight."
        );

        assertTrue(
                workoutReportPage.isActivitiesBlockDisplayed(),
                "Calories, steps and pulse block is not displayed."
        );

        assertTrue(
                workoutReportPage.isShareButtonDisplayed(),
                "Report share button is not displayed."
        );
        workoutReportPage.closeReport();
    }

    private void registerNewUser() {
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

        if (accountDeletionFlow.canDeleteAccount()) {
            accountDeletionFlow.deleteAccount();
        }
    }
}