package flows;

import io.appium.java_client.android.AndroidDriver;
import pages.android.GoogleHealthPage;
import pages.android.HealthPermissionsPage;

public class GoogleHealthAccessFlow {

    private final GoogleHealthPage googleHealthPage;
    private final HealthPermissionsPage healthPermissionsPage;

    public GoogleHealthAccessFlow(AndroidDriver driver) {
        googleHealthPage =
                new GoogleHealthPage(driver);
        healthPermissionsPage =
                new HealthPermissionsPage(driver);
    }

    public void grantAccess() {
        googleHealthPage.grantAccess();
        healthPermissionsPage.enableAllowAll();
        healthPermissionsPage.confirmHealthPermissions();
    }

    public void continueWithoutHealthConnect() {
        googleHealthPage.skipAccess();
        googleHealthPage.declinePersuasion();
        googleHealthPage.continueWithoutAccess();
    }

    public void continueDependingOnAvailability(
            boolean healthConnectAvailable
    ) {
        if (healthConnectAvailable) {
            grantAccess();
            return;
        }

        continueWithoutHealthConnect();
    }
}