import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    public void testSendAndVerifyNotification() {
        String userId = "user123";
        String message = "Test notification message";

        // Send notification
        boolean sendResult = componentA.sendNotification(userId, message);
        assertTrue(sendResult, "Notification should be sent successfully");

        // Retrieve logs and verify notification
        List<String> logs = componentA.getNotificationLogs();
        boolean verifyResult = componentB.verifyNotificationReceived(logs, userId, message);
        assertTrue(verifyResult, "Notification should be verified successfully");
    }

    @Test
    public void testSendNotificationWithNullValues() {
        boolean sendResult = componentA.sendNotification(null, null);
        assertFalse(sendResult, "Sending notification with null values should fail");
    }

    @Test
    public void testVerifyNotificationWithNullValues() {
        boolean verifyResult = componentB.verifyNotificationReceived(null, null, null);
        assertFalse(verifyResult, "Verifying notification with null values should fail");
    }
}