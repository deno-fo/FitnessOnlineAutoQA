package extensions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.opentest4j.TestAbortedException;
import utils.IosDevice;
import utils.IosDeviceContext;
import utils.IosDeviceQuarantine;
import utils.IosTestResults;

import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.*;

class DeviceQuarantineTest {
    @Test
    void preservesOriginalFailureAndAbortsNextTestBeforeSetup() {
        FailingFixture.quarantine = new IosDeviceQuarantine();
        FailingFixture.setups = 0;
        var listener = new SummaryGeneratingListener();
        var request = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectClass(FailingFixture.class))
                .configurationParameter("junit.jupiter.execution.parallel.enabled", "false")
                .build();
        LauncherFactory.create().execute(request, listener);

        var summary = listener.getSummary();
        assertEquals(1, summary.getTestsFailedCount());
        assertEquals(1, summary.getTestsAbortedCount());
        assertEquals(1, FailingFixture.setups);
        Throwable original = summary.getFailures().get(0).getException();
        assertEquals("original scenario failure", original.getMessage());
        assertTrue(java.util.Arrays.stream(original.getSuppressed())
                .anyMatch(error -> error.getMessage().equals("cleanup failure")));
        assertNull(IosDeviceContext.get());
    }

    @Test
    void blocksOnlyAffectedDeviceAndOnlyWithinCurrentRun() throws Exception {
        IosDeviceQuarantine quarantine = new IosDeviceQuarantine();
        quarantine.block("broken", "cleanup failed");
        var blocked = invocation("broken", quarantine);
        assertThrows(TestAbortedException.class, () -> blocked.beforeEach(context()));
        blocked.afterEach(context());

        var healthy = invocation("healthy", quarantine);
        healthy.beforeEach(context());
        assertEquals("healthy", IosDeviceContext.getRequired().udid());
        healthy.afterEach(context());

        var nextRun = invocation("broken", new IosDeviceQuarantine());
        nextRun.beforeEach(context());
        nextRun.afterEach(context());
        assertNull(IosDeviceContext.get());
    }

    @Test
    void successfulCleanupAllowsFollowingInvocation() throws Exception {
        var quarantine = new IosDeviceQuarantine();
        var first = invocation("healthy", quarantine);
        first.beforeEach(context());
        first.afterEach(context());
        var second = invocation("healthy", quarantine);
        second.beforeEach(context());
        second.afterEach(context());
        assertNull(quarantine.reason("healthy"));
    }

    private static IosDeviceTestExtension.IosDeviceInvocationExtension invocation(
            String udid, IosDeviceQuarantine quarantine
    ) {
        return new IosDeviceTestExtension.IosDeviceInvocationExtension(
                new IosDevice(udid, udid, 8100, "/tmp/unused-wda"),
                new IosTestResults(), quarantine
        );
    }

    private static ExtensionContext context() {
        return (ExtensionContext) Proxy.newProxyInstance(
                ExtensionContext.class.getClassLoader(),
                new Class<?>[]{ExtensionContext.class},
                (proxy, method, arguments) -> switch (method.getName()) {
                    case "getRequiredTestClass" -> FailingFixture.class;
                    case "getRequiredTestMethod" -> FailingFixture.class.getDeclaredMethod("first");
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @ExtendWith(FixtureExtension.class)
    static class FailingFixture {
        static IosDeviceQuarantine quarantine;
        static int setups;
        private boolean started;

        @BeforeEach
        void setup() {
            setups++;
            started = true;
        }

        @Test @Order(1)
        void first() {
            fail("original scenario failure");
        }

        @Test @Order(2)
        void second() {
            fail("must never run");
        }

        @AfterEach
        void cleanup() {
            if (started) {
                IosDeviceContext.markCleanupFailed("cleanup failure");
                throw new IllegalStateException("cleanup failure");
            }
        }
    }

    static class FixtureExtension implements BeforeEachCallback, AfterEachCallback {
        private final IosDeviceTestExtension.IosDeviceInvocationExtension delegate =
                invocation("broken", FailingFixture.quarantine);

        @Override
        public void beforeEach(ExtensionContext context) throws Exception {
            delegate.beforeEach(context);
        }

        @Override
        public void afterEach(ExtensionContext context) {
            delegate.afterEach(context);
        }
    }
}
