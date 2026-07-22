package tests.ios;

import flows.ios.IosAccountDeletionFlow;
import flows.ios.IosCustomWorkoutCreationFlow;
import flows.ios.IosPostLoginFlow;
import flows.ios.IosWorkoutExecutionFlow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.ios.EmailRegistrationPage;
import pages.ios.LoginPage;
import pages.ios.WorkoutReportPage;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IosCustomWorkoutTest
        extends BaseIosTest {

    private LoginPage loginPage;

    private EmailRegistrationPage
            registrationPage;

    private IosCustomWorkoutCreationFlow
            workoutCreationFlow;

    private IosWorkoutExecutionFlow
            workoutExecutionFlow;

    private IosPostLoginFlow postLoginFlow;

    private IosAccountDeletionFlow
            accountDeletionFlow;

    private WorkoutReportPage reportPage;

    @BeforeEach
    public void createPagesAndFlows() {
        loginPage =
                new LoginPage(driver);

        registrationPage =
                new EmailRegistrationPage(driver);

        workoutCreationFlow =
                new IosCustomWorkoutCreationFlow(
                        driver
                );

        workoutExecutionFlow =
                new IosWorkoutExecutionFlow(
                        driver
                );

        postLoginFlow =
                new IosPostLoginFlow(driver);

        accountDeletionFlow =
                new IosAccountDeletionFlow(
                        driver
                );

        reportPage =
                new WorkoutReportPage(driver);
    }

    @Test
    public void shouldCreateAndCompleteCustomWorkout() {
        loginPage.openEmailAuthentication();

        registrationPage.registerMaleUser(
                TestData.uniqueEmail(),
                TestData.PASSWORD,
                TestData.NAME,
                TestData.SURNAME
        );

        postLoginFlow.completeUntilMainScreen();

        workoutCreationFlow.createProgram(
                TestData.CUSTOM_WORKOUT_NAME
        );

        workoutCreationFlow.addWorkoutDay(
                TestData.CUSTOM_WORKOUT_DAY_NAME,
                TestData.EXERCISE_NAME,
                TestData.EXERCISE_SETS,
                TestData.EXERCISE_REPEATS,
                TestData.EXERCISE_WEIGHT
        );

        assertTrue(
                workoutCreationFlow.isWorkoutReady(
                        TestData.EXERCISE_NAME,
                        TestData.EXERCISE_SETS,
                        TestData.EXERCISE_REPEATS,
                        TestData.EXERCISE_WEIGHT
                ),
                "Custom workout creation failed: "
                        + "the exercise or its parameters "
                        + "were not displayed."
        );

        int numberOfSets =
                Integer.parseInt(
                        TestData.EXERCISE_SETS
                );

        int repeats =
                Integer.parseInt(
                        TestData.EXERCISE_REPEATS
                );

        int weight =
                Integer.parseInt(
                        TestData.EXERCISE_WEIGHT
                );

        workoutExecutionFlow.completeWorkout(
                numberOfSets,
                TestData.EXERCISE_WEIGHT,
                TestData.EXERCISE_REPEATS
        );

        int expectedRepetitions =
                numberOfSets * repeats;

        int expectedLiftedWeight =
                numberOfSets
                        * repeats
                        * weight
                        * 2;

        assertTrue(
                reportPage.isOpened(),
                "Workout report was not opened."
        );

        assertEquals(
                "100%",
                reportPage.getCompletionPercentage(),
                "Workout completion percentage "
                        + "is incorrect."
        );

        assertTrue(
                reportPage.hasExpectedRepetitions(
                        expectedRepetitions
                ),
                "Completed repetitions result "
                        + "is incorrect."
        );

        assertTrue(
                reportPage.hasExpectedLiftedWeight(
                        expectedLiftedWeight
                ),
                "Lifted weight result is incorrect."
        );

        assertTrue(
                reportPage.hasActivityMetrics(),
                "Calories, steps or pulse block "
                        + "is missing."
        );

        reportPage.closeIfPresent();
    }

    @AfterEach
    public void cleanUpCreatedAccount() {
        if (reportPage != null) {
            reportPage.closeIfPresent();
        }

        if (accountDeletionFlow != null
                && accountDeletionFlow
                .deleteAccountIfPossible()) {

            assertTrue(
                    loginPage
                            .isEmailAuthenticationOptionDisplayed(),
                    "Account deletion failed: "
                            + "authentication options screen "
                            + "was not opened."
            );
        }
    }
}