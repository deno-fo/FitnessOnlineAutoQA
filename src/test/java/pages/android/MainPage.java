package pages.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.WebElement;

public class MainPage extends AndroidBasePage {

    private final By bottomNavigation =
            id("bottom_navigation");

    private final By workoutProgramsList =
            id("recycler_view");

    private final By courseTitle =
            id("textCourseTitle");

    private final By courseLocation =
            id("textCourseLocation");

    private final By courseComplexity =
            id("textComplexity");

    private final By workoutsTab =
            AppiumBy.accessibilityId("Workouts");

    private boolean isDisplayedWithoutWait(
            By locator
    ) {
        return driver.findElements(locator)
                .stream()
                .anyMatch(WebElement::isDisplayed);
    }

    public void openWorkoutsTab() {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        workoutsTab
                )
        ).click();
    }

    public MainPage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isBottomNavigationDisplayed() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        bottomNavigation
                )
        ).isDisplayed();
    }

    public boolean isWorkoutProgramsListDisplayed() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        workoutProgramsList
                )
        ).isDisplayed();
    }

    public boolean isGuestHomeReady() {
        return isDisplayedWithoutWait(bottomNavigation)
                && isDisplayedWithoutWait(workoutProgramsList);
    }

    public boolean isReady() {
        return isDisplayedWithoutWait(
                bottomNavigation
        );
    }

    public String getCourseTitle() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        courseTitle
                )
        ).getText();
    }

    public String getCourseLocation() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        courseLocation
                )
        ).getText();
    }

    public String getCourseComplexity() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        courseComplexity
                )
        ).getText();
    }

    public boolean isPreMadeWorkoutSelected(
            String expectedTitle,
            String expectedLocation,
            String expectedComplexity
    ) {
        return getCourseTitle().equals(expectedTitle)
                && getCourseLocation().equals(expectedLocation)
                && getCourseComplexity().equals(expectedComplexity);
    }
}