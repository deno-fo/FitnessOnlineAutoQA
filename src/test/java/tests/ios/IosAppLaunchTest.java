package tests.ios;

import io.appium.java_client.appmanagement.ApplicationState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IosAppLaunchTest extends BaseIosTest {

    @Test
    public void shouldLaunchFitnessOnlineApp() {
        ApplicationState applicationState =
                driver.queryAppState(
                        "com.aiti.FitnessOnline"
                );

        assertEquals(
                ApplicationState.RUNNING_IN_FOREGROUND,
                applicationState,
                "FitnessOnline app was not launched in foreground."
        );
    }
}