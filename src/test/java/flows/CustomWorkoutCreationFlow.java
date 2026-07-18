package flows;

import components.android.TutorialOverlay;
import io.appium.java_client.android.AndroidDriver;
import pages.android.CustomWorkoutCreationPage;
import pages.android.ExerciseSelectionPage;
import pages.android.ExerciseSettingsPage;
import pages.android.MainPage;
import pages.android.WorkoutDayEditorPage;
import pages.android.WorkoutDaysPage;

public class CustomWorkoutCreationFlow {

    private final CustomWorkoutSelectionFlow customWorkoutSelectionFlow;
    private final CustomWorkoutCreationPage customWorkoutCreationPage;
    private final TutorialOverlay tutorialOverlay;
    private final MainPage mainPage;
    private final WorkoutDaysPage workoutDaysPage;
    private final WorkoutDayEditorPage workoutDayEditorPage;
    private final ExerciseSelectionPage exerciseSelectionPage;
    private final ExerciseSettingsPage exerciseSettingsPage;

    public CustomWorkoutCreationFlow(AndroidDriver driver) {
        customWorkoutSelectionFlow =
                new CustomWorkoutSelectionFlow(driver);

        customWorkoutCreationPage =
                new CustomWorkoutCreationPage(driver);

        tutorialOverlay =
                new TutorialOverlay(driver);

        mainPage =
                new MainPage(driver);

        workoutDaysPage =
                new WorkoutDaysPage(driver);

        workoutDayEditorPage =
                new WorkoutDayEditorPage(driver);

        exerciseSelectionPage =
                new ExerciseSelectionPage(driver);

        exerciseSettingsPage =
                new ExerciseSettingsPage(driver);
    }

    public void createWorkoutWithOneExercise(
            String workoutName,
            String workoutDayName,
            String exerciseName,
            String sets,
            String repeats,
            String weight
    ) throws InterruptedException {

        customWorkoutSelectionFlow.openWorkoutBuilder();

        customWorkoutCreationPage.createWorkout(
                workoutName
        );

        tutorialOverlay.dismiss();

        mainPage.openWorkoutsTab();
        workoutDaysPage.addWorkoutDay();

        workoutDayEditorPage.enterWorkoutDayName(
                workoutDayName
        );

        workoutDayEditorPage.openExerciseSelection();
        exerciseSelectionPage.addFirstChestExercise();

        tutorialOverlay.dismiss();

        workoutDayEditorPage.openExerciseSettings(
                exerciseName
        );

        exerciseSettingsPage.configureExercise(
                sets,
                repeats,
                weight
        );

        workoutDayEditorPage.saveWorkoutDay();
    }
}