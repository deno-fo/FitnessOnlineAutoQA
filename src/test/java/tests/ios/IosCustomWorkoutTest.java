package tests.ios;

import annotations.IosDeviceTest;
import flows.ios.IosAccountDeletionFlow;
import flows.ios.IosCustomWorkoutCreationFlow;
import flows.ios.IosPostLoginFlow;
import flows.ios.IosWorkoutExecutionFlow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import pages.ios.EmailRegistrationPage;
import pages.ios.LoginPage;
import pages.ios.WorkoutReportPage;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IosCustomWorkoutTest
        extends BaseIosTest {

    private LoginPage loginPage;

    private EmailRegistrationPage registrationPage;

    private IosCustomWorkoutCreationFlow workoutCreationFlow;

    private IosWorkoutExecutionFlow workoutExecutionFlow;

    private IosPostLoginFlow postLoginFlow;

    private IosAccountDeletionFlow accountDeletionFlow;

    private WorkoutReportPage reportPage;

    private long testStartTime;

    private boolean reportClosed;

    @BeforeEach
    public void createPagesAndFlows() {

        testStartTime =
                System.currentTimeMillis();

        reportClosed = false;

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

    @IosDeviceTest
    public void shouldCreateAndCompleteCustomWorkout() {

        logTime("START TEST");

        loginPage.openEmailAuthentication();

        logTime("Opened email authentication");

        registrationPage.registerMaleUser(
                TestData.uniqueEmail(),
                TestData.PASSWORD,
                TestData.NAME,
                TestData.SURNAME
        );

        logTime("Registration finished");

        postLoginFlow.completeUntilMainScreen();

        logTime("Main screen reached");

        workoutCreationFlow.createProgram(
                TestData.CUSTOM_WORKOUT_NAME
        );

        logTime("Program created");

        workoutCreationFlow.addWorkoutDay(
                TestData.CUSTOM_WORKOUT_DAY_NAME,
                TestData.EXERCISE_NAME,
                TestData.EXERCISE_SETS,
                TestData.EXERCISE_REPEATS,
                TestData.EXERCISE_WEIGHT
        );

        logTime("Workout day created");

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

        logTime("Workout ready check passed");

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

        logTime("Workout completed");

        int expectedRepetitions =
                numberOfSets * repeats;

        int expectedLiftedWeight =
                numberOfSets
                        * repeats
                        * weight
                        * 2;

        /*
         * completeWorkout() возвращает управление
         * только после появления отчёта.
         */
        logTime("Report opened");

        assertEquals(
                "100%",
                reportPage.getCompletionPercentage(),
                "Workout completion percentage "
                        + "is incorrect."
        );

        logTime("Percentage checked");

        assertTrue(
                reportPage.hasExpectedRepetitions(
                        expectedRepetitions
                ),
                "Completed repetitions result "
                        + "is incorrect."
        );

        logTime("Repetitions checked");

        assertTrue(
                reportPage.hasExpectedLiftedWeight(
                        expectedLiftedWeight
                ),
                "Lifted weight result is incorrect."
        );

        logTime("Weight checked");

        assertTrue(
                reportPage.hasActivityMetrics(),
                "Calories, steps or pulse block "
                        + "is missing."
        );

        logTime("Metrics checked");

        reportPage.close();

        reportClosed = true;

        logTime("Report closed");
    }

    @AfterEach
    public void cleanUpCreatedAccount() {

        logTime("Cleanup started");

        if (reportPage != null
                && !reportClosed) {

            reportPage.closeIfPresent();
        }

        if (accountDeletionFlow != null
                && accountDeletionFlow
                .deleteAccountIfPossible()) {

            logTime("Account deleted");

            assertTrue(
                    loginPage
                            .isEmailAuthenticationOptionDisplayed(),
                    "Account deletion failed: "
                            + "authentication options screen "
                            + "was not opened."
            );
        }

        logTime("Cleanup finished");
    }

    private void logTime(
            String message
    ) {

        long elapsed =
                System.currentTimeMillis()
                        - testStartTime;

        System.out.println(
                "[TEST "
                        + elapsed
                        + " ms] "
                        + message
        );
    }
}
