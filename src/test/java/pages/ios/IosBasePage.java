package pages.ios;

import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class IosBasePage {

    protected final IOSDriver driver;
    protected final WebDriverWait wait;

    public IosBasePage(IOSDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );
    }
}