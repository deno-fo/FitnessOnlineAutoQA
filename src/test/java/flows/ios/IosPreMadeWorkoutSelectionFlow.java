package flows.ios;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Map;

public class IosPreMadeWorkoutSelectionFlow {

    private final IOSDriver driver;
    private final WebDriverWait wait;

    private final By generalMuscleBuildingProgram =
            AppiumBy.accessibilityId(
                    "General muscle building"
            );

    private final By gymTrainingOption =
            AppiumBy.accessibilityId(
                    "In the gym"
            );

    private final By expertDifficultyOption =
            AppiumBy.accessibilityId(
                    "Expert"
            );

    private final By selectedWorkoutCategories =
            AppiumBy.accessibilityId(
                    "for men, for the gym, expert"
            );

    public IosPreMadeWorkoutSelectionFlow(
            IOSDriver driver
    ) {
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );
    }

    public void selectGeneralMuscleBuildingForGymAtExpertLevel() {
        click(generalMuscleBuildingProgram);
        click(gymTrainingOption);
        click(expertDifficultyOption);

        WebElement categories =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                selectedWorkoutCategories
                        )
                );

        tapAddButtonBelow(categories);

        wait.until(
                ExpectedConditions.stalenessOf(
                        categories
                )
        );
    }

    private void click(By locator) {
        wait.until(
                ExpectedConditions.elementToBeClickable(
                        locator
                )
        ).click();
    }

    private void tapAddButtonBelow(
            WebElement categories
    ) {
        Rectangle categoriesRect =
                categories.getRect();

        int x =
                categoriesRect.getX()
                        + categoriesRect.getWidth() / 2;

        int y =
                categoriesRect.getY()
                        + categoriesRect.getHeight()
                        + 76;

        driver.executeScript(
                "mobile: tap",
                Map.of(
                        "x", x,
                        "y", y
                )
        );
    }
}