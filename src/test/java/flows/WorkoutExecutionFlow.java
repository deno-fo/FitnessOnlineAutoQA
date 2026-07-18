package flows;

import components.android.NearbyDevicesPermissionDialog;
import components.android.TutorialOverlay;
import io.appium.java_client.android.AndroidDriver;
import pages.android.WorkoutDayDetailsPage;
import pages.android.WorkoutDaysPage;
import pages.android.WorkoutExecutionPage;

public class WorkoutExecutionFlow {

    private final WorkoutDaysPage workoutDaysPage;
    private final WorkoutDayDetailsPage workoutDayDetailsPage;
    private final NearbyDevicesPermissionDialog nearbyDevicesPermissionDialog;
    private final WorkoutExecutionPage workoutExecutionPage;
    private final TutorialOverlay tutorialOverlay;

    public WorkoutExecutionFlow(AndroidDriver driver) {
        workoutDaysPage =
                new WorkoutDaysPage(driver);

        workoutDayDetailsPage =
                new WorkoutDayDetailsPage(driver);

        nearbyDevicesPermissionDialog =
                new NearbyDevicesPermissionDialog(driver);

        workoutExecutionPage =
                new WorkoutExecutionPage(driver);

        tutorialOverlay =
                new TutorialOverlay(driver);
    }

    public void completeWorkout(
            String workoutDayName,
            String weight,
            String repeats,
            int sets
    ) throws InterruptedException {

        workoutDaysPage.openWorkoutDay(
                workoutDayName
        );

        workoutDayDetailsPage.beginWorkout();

        nearbyDevicesPermissionDialog.allowNearbyDevices();

        for (int set = 0; set < sets; set++) {
            workoutExecutionPage.recordSet(
                    weight,
                    repeats
            );
        }

        workoutExecutionPage.finishWorkout();

        tutorialOverlay.dismissIfPresent();
    }
}