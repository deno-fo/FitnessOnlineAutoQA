package tests.ios;

import flows.ios.IosAccountDeletionFlow;
import flows.ios.IosCustomWorkoutCreationFlow;
import flows.ios.IosPostLoginFlow;
import flows.ios.IosWorkoutDayEditAndDeleteFlow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import annotations.IosDeviceTest;
import pages.ios.EmailRegistrationPage;
import pages.ios.LoginPage;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IosWorkoutDayEditAndDeleteTest
        extends BaseIosTest {

    private LoginPage loginPage;

    private EmailRegistrationPage registrationPage;

    private IosPostLoginFlow postLoginFlow;

    private IosCustomWorkoutCreationFlow
            workoutCreationFlow;

    private IosWorkoutDayEditAndDeleteFlow
            workoutEditAndDeleteFlow;

    private IosAccountDeletionFlow
            accountDeletionFlow;

    @BeforeEach
    public void createPagesAndFlows() {
        loginPage =
                new LoginPage(driver);

        registrationPage =
                new EmailRegistrationPage(driver);

        postLoginFlow =
                new IosPostLoginFlow(driver);

        workoutCreationFlow =
                new IosCustomWorkoutCreationFlow(
                        driver
                );

        workoutEditAndDeleteFlow =
                new IosWorkoutDayEditAndDeleteFlow(
                        driver
                );

        accountDeletionFlow =
                new IosAccountDeletionFlow(
                        driver
                );
    }

    @IosDeviceTest
    public void shouldEditAndDeleteWorkoutDay() {
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

        workoutEditAndDeleteFlow.editWorkoutDay(
                TestData.EXERCISE_NAME,
                TestData.UPDATED_WORKOUT_DAY_NAME,
                TestData.UPDATED_EXERCISE_SETS,
                TestData.UPDATED_EXERCISE_REPEATS,
                TestData.UPDATED_EXERCISE_WEIGHT
        );

        assertTrue(
                workoutEditAndDeleteFlow
                        .hasUpdatedWorkoutData(
                                TestData
                                        .UPDATED_WORKOUT_DAY_NAME,
                                TestData
                                        .UPDATED_EXERCISE_SETS,
                                TestData
                                        .UPDATED_EXERCISE_REPEATS,
                                TestData
                                        .UPDATED_EXERCISE_WEIGHT
                        ),
                "Workout day name or exercise "
                        + "parameters were not updated."
        );

        workoutEditAndDeleteFlow
                .deleteWorkoutDay();

        assertTrue(
                workoutEditAndDeleteFlow
                        .isWorkoutDayDeleted(),
                "Workout day was not deleted."
        );
    }

    @AfterEach
    public void cleanUpCreatedAccount() {
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