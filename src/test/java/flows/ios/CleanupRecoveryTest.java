package flows.ios;

import io.appium.java_client.ios.IOSDriver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.Response;
import pages.ios.WorkoutReportPage;

import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CleanupRecoveryTest {
    @Test
    void reportWaitsForDelayedActivityMetric() throws Exception {
        FakeDriver driver = new FakeDriver("metrics delayed");
        WorkoutReportPage report = new WorkoutReportPage(driver);
        assertTrue(report.hasActivityMetrics());
        assertTrue(driver.pulseChecks > 1);
        assertTrue(report.getMissingActivityMetrics().isEmpty());
    }

    @Test
    void reportDoesNotAcceptPermanentlyMissingMetric() throws Exception {
        FakeDriver driver = new FakeDriver("metrics missing");
        WorkoutReportPage report = new WorkoutReportPage(driver);
        assertFalse(report.hasActivityMetrics());
        assertEquals(List.of("Pulse"), report.getMissingActivityMetrics());
    }

    @Test
    void signedOutAccountNeedsNoDeletionOrRestart() throws Exception {
        FakeDriver driver = new FakeDriver("login");
        assertFalse(new IosCleanupRecovery(driver).openAccountMenu());
        assertTrue(driver.actions.isEmpty());
    }

    @Test
    void unfinishedOnboardingContinuesToAccountMenu() throws Exception {
        FakeDriver driver = new FakeDriver("gender");
        assertTrue(new IosCleanupRecovery(driver).openAccountMenu());
        assertEquals(List.of("Male", "CONTINUE", "More"), driver.actions);
    }

    @Test
    void editorIsRecoveredWithOneRestart() throws Exception {
        FakeDriver driver = new FakeDriver("editor");
        assertTrue(new IosCleanupRecovery(driver).openAccountMenu());
        assertEquals(List.of("terminate", "activate", "More"), driver.actions);
    }

    @Test
    void existingAccountCannotBeArmedForDeletion() throws Exception {
        FakeDriver driver = new FakeDriver("main");
        IosAccountDeletionFlow deletion = new IosAccountDeletionFlow(driver);
        assertThrows(IllegalStateException.class, deletion::beforeCreatingTestAccount);
        assertFalse(deletion.deleteAccountIfPossible());
        assertTrue(driver.actions.isEmpty());
    }

    @Test
    void successfulLoginCanBeLoggedOutWithoutSuccessFlag() throws Exception {
        FakeDriver driver = new FakeDriver("main");
        new IosLogoutFlow(driver).logOutAfterInterruptedScenario();
        assertEquals(List.of("More", "Log out", "Logout"), driver.actions);
        assertEquals("login", driver.screen);
    }

    @Test
    void createdAccountIsDeletedAfterFailureOnReport() throws Exception {
        FakeDriver driver = new FakeDriver("form");
        IosAccountDeletionFlow deletion = new IosAccountDeletionFlow(driver);
        deletion.beforeCreatingTestAccount();
        // Registration succeeded; the scenario subsequently failed on its report assertion.
        driver.screen = "report";
        assertTrue(deletion.deleteAccountIfPossible());
        assertEquals(List.of("close report", "More", "Settings", "Delete account",
                "DELETE", "confirm deletion"), driver.actions);
        assertEquals("login", driver.screen);
        assertFalse(deletion.deleteAccountIfPossible());
    }

    private static final class FakeDriver extends IOSDriver {
        private String screen;
        private int pulseChecks;
        private final List<String> actions = new ArrayList<>();

        FakeDriver(String screen) throws Exception {
            super(new HttpCommandExecutor(URI.create("http://localhost:1").toURL()) {
                @Override
                public Response execute(Command command) {
                    Response response = new Response();
                    response.setState("success");
                    response.setStatus(0);
                    response.setSessionId("fake-cleanup");
                    response.setValue(switch (command.getName()) {
                        case "newSession" -> Map.of("platformName", "iOS");
                        case "getWindowRect", "getCurrentWindowSize" -> Map.of("x", 0, "y", 0, "width", 440, "height", 956);
                        default -> throw new AssertionError("Unexpected command: " + command.getName());
                    });
                    return response;
                }
            }, new MutableCapabilities(Map.of("platformName", "iOS")));
            this.screen = screen;
        }

        @Override
        public WebElement findElement(By by) {
            return findElements(by).stream().findFirst()
                    .orElseThrow(() -> new NoSuchElementException(by.toString()));
        }

        @Override
        public List<WebElement> findElements(By by) {
            String locator = by.toString();
            if (screen.startsWith("metrics") && locator.equals("AppiumBy.accessibilityId: Pulse")) {
                pulseChecks++;
            }
            String label = switch (screen) {
                case "metrics delayed", "metrics missing" ->
                        locator.equals("AppiumBy.accessibilityId: Calories") ? "Calories"
                        : locator.equals("AppiumBy.accessibilityId: Steps") ? "Steps"
                        : locator.equals("AppiumBy.accessibilityId: Pulse")
                        && screen.equals("metrics delayed") && pulseChecks > 1 ? "Pulse" : null;
                case "login" -> locator.contains("Sign in/Sign up with email") ? "login" : null;
                case "form" -> locator.contains("XCUIElementTypeSecureTextField")
                        || locator.equals("AppiumBy.accessibilityId: Sign in") ? "form" : null;
                case "gender" -> locator.equals("AppiumBy.accessibilityId: Male") ? "Male" : null;
                case "body" -> locator.equals("AppiumBy.accessibilityId: CONTINUE") ? "CONTINUE" : null;
                case "main" -> locator.equals("AppiumBy.accessibilityId: More") ? "More" : null;
                case "more" -> locator.equals("AppiumBy.accessibilityId: Log out") ? "Log out"
                        : locator.equals("AppiumBy.accessibilityId: Settings") ? "Settings" : null;
                case "confirm" -> locator.contains("name == 'Logout'") ? "Logout" : null;
                case "report" -> locator.equals("AppiumBy.accessibilityId: Calories") ? "Calories" : null;
                case "settings" -> locator.equals("AppiumBy.accessibilityId: Delete account") ? "Delete account" : null;
                case "delete" -> locator.contains("name == 'DELETE'") ? "DELETE" : null;
                default -> null;
            };
            if (label == null) {
                return List.of();
            }
            return List.of((WebElement) Proxy.newProxyInstance(
                    WebElement.class.getClassLoader(), new Class<?>[]{WebElement.class},
                    (proxy, method, arguments) -> switch (method.getName()) {
                        case "isDisplayed", "isEnabled" -> true;
                        case "getRect" -> new Rectangle(100, 100, 40, 100);
                        case "click" -> {
                            actions.add(label);
                            screen = switch (label) {
                                case "Male" -> "body";
                                case "CONTINUE" -> "main";
                                case "More" -> "more";
                                case "Log out" -> "confirm";
                                case "Logout" -> "login";
                                case "Settings" -> "settings";
                                case "Delete account" -> "delete";
                                default -> screen;
                            };
                            yield null;
                        }
                        default -> throw new AssertionError(method.getName());
                    }));
        }

        @Override
        public TargetLocator switchTo() {
            return (TargetLocator) Proxy.newProxyInstance(
                    TargetLocator.class.getClassLoader(), new Class<?>[]{TargetLocator.class},
                    (proxy, method, arguments) -> {
                        if (screen.equals("confirm") || screen.equals("delete confirmation")) {
                            return Proxy.newProxyInstance(Alert.class.getClassLoader(),
                                    new Class<?>[]{Alert.class}, (alert, action, values) -> {
                                        if (action.getName().equals("accept")) {
                                            actions.add("Logout");
                                            screen = "login";
                                        }
                                        return null;
                                    });
                        }
                        throw new NoAlertPresentException();
                    });
        }

        @Override
        public boolean terminateApp(String bundleId) {
            actions.add("terminate");
            return true;
        }

        @Override
        public void activateApp(String bundleId) {
            actions.add("activate");
            screen = "main";
        }

        @Override
        public Object executeScript(String script, Object... arguments) {
            switch (script) {
                case "mobile: terminateApp" -> actions.add("terminate");
                case "mobile: activateApp" -> {
                    actions.add("activate");
                    screen = "main";
                }
                case "mobile: tap" -> {
                    switch (screen) {
                        case "report" -> { actions.add("close report"); screen = "main"; }
                        case "delete" -> { actions.add("DELETE"); screen = "delete confirmation"; }
                        case "delete confirmation" -> { actions.add("confirm deletion"); screen = "login"; }
                        default -> throw new AssertionError("Unexpected tap on " + screen);
                    }
                }
                default -> throw new AssertionError("Unexpected script: " + script);
            }
            return true;
        }
    }
}
