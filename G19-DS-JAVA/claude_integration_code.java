import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {
    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    void testSuccessfulNotificationFlow() {
        String userId = "user123";
        String message = "Test notification";

        // Send notification using ComponentA
        boolean sendResult = componentA.sendNotification(userId, message);
        assertTrue(sendResult, "Notification should be sent successfully");

        // Verify notification using ComponentB
        boolean verifyResult = componentB.verifyNotificationReceived(
                componentA.getNotificationLogs(),
                userId,
                message);
        assertTrue(verifyResult, "Notification should be verified in logs");
    }

    @Test
    void testNullInputHandling() {
        // Test null userId
        assertFalse(componentA.sendNotification(null, "message"));
        assertFalse(componentB.verifyNotificationReceived(
                componentA.getNotificationLogs(),
                null,
                "message"));

        // Test null message
        assertFalse(componentA.sendNotification("user123", null));
        assertFalse(componentB.verifyNotificationReceived(
                componentA.getNotificationLogs(),
                "user123",
                null));

        // Test null logs
        assertFalse(componentB.verifyNotificationReceived(
                null,
                "user123",
                "message"));
    }

    @Test
    void testNonExistentNotification() {
        String userId = "user123";
        String message = "Test notification";

        // Send notification
        componentA.sendNotification(userId, message);

        // Verify with wrong message
        boolean verifyResult = componentB.verifyNotificationReceived(
                componentA.getNotificationLogs(),
                userId,
                "Wrong message");
        assertFalse(verifyResult, "Should not verify with incorrect message");

        // Verify with wrong userId
        verifyResult = componentB.verifyNotificationReceived(
                componentA.getNotificationLogs(),
                "wrongUser",
                message);
        assertFalse(verifyResult, "Should not verify with incorrect userId");
    }

    @Test
    void testMultipleNotifications() {
        String userId = "user123";
        componentA.sendNotification(userId, "Message 1");
        componentA.sendNotification(userId, "Message 2");
        componentA.sendNotification("otherUser", "Message 3");

        assertTrue(componentB.verifyNotificationReceived(
                componentA.getNotificationLogs(),
                userId,
                "Message 1"));
        assertTrue(componentB.verifyNotificationReceived(
                componentA.getNotificationLogs(),
                userId,
                "Message 2"));
        assertTrue(componentB.verifyNotificationReceived(
                componentA.getNotificationLogs(),
                "otherUser",
                "Message 3"));
    }
}