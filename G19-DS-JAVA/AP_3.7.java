import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class NotificationIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    void setUp() {
        // Initialize real components for integration testing
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    @DisplayName("Verify notification is sent and received successfully")
    void testNotificationSuccessPath() {
        // Test data
        String userId = "user123";
        String message = "Hello, this is a test notification!";

        // Step 1: Send notification using ComponentA
        boolean notificationSent = componentA.sendNotification(userId, message);

        // Step 2: Get notification logs from ComponentA
        List<String> logs = componentA.getNotificationLogs();

        // Step 3: Verify notification using ComponentB
        boolean notificationFound = componentB.verifyNotificationReceived(logs, userId, message);

        // Assertions
        assertTrue(notificationSent, "Notification should be sent successfully");
        assertTrue(notificationFound, "ComponentB should verify notification was received");
        assertEquals(1, logs.size(), "There should be exactly one notification log");
    }

    @Test
    @DisplayName("Verify notification verification fails when notification was not sent")
    void testNotificationMismatch() {
        // Step 1: Send a notification
        componentA.sendNotification("user123", "Original message");

        // Step 2: Get notification logs
        List<String> logs = componentA.getNotificationLogs();

        // Step 3: Try to verify a different notification that wasn't sent
        boolean notificationFound = componentB.verifyNotificationReceived(
                logs, "user123", "Different message");

        // Assertion
        assertFalse(notificationFound, "Verification should fail for a message that wasn't sent");
    }

    @Test
    @DisplayName("Verify notification verification fails for a different user")
    void testWrongUserVerification() {
        // Step 1: Send a notification to user123
        componentA.sendNotification("user123", "Test message");

        // Step 2: Get notification logs
        List<String> logs = componentA.getNotificationLogs();

        // Step 3: Try to verify notification for a different user
        boolean notificationFound = componentB.verifyNotificationReceived(
                logs, "differentUser", "Test message");

        // Assertion
        assertFalse(notificationFound, "Verification should fail for a different user ID");
    }

    @Nested
    @DisplayName("Tests for edge cases and null values")
    class EdgeCaseTests {

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = { "", " " })
        @DisplayName("Sending notification with invalid user IDs should fail")
        void testSendNotificationWithInvalidUserId(String userId) {
            // Try to send notification with invalid user ID
            boolean result = componentA.sendNotification(userId, "Test message");

            // Assertion
            assertFalse(result, "Sending notification with invalid userId should fail");
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = { "", " " })
        @DisplayName("Sending notification with invalid messages should fail")
        void testSendNotificationWithInvalidMessage(String message) {
            // Try to send notification with invalid message
            boolean result = componentA.sendNotification("user123", message);

            // Assertion
            assertFalse(result, "Sending notification with invalid message should fail");
        }

        @Test
        @DisplayName("Verify notification with null logs should fail")
        void testVerifyNotificationWithNullLogs() {
            boolean result = componentB.verifyNotificationReceived(null, "user123", "test");
            assertFalse(result, "Verification with null logs should fail");
        }

        @Test
        @DisplayName("End to end test with multiple notifications")
        void testMultipleNotifications() {
            // Send multiple notifications
            componentA.sendNotification("user1", "Message 1");
            componentA.sendNotification("user2", "Message 2");
            componentA.sendNotification("user1", "Message 3");

            List<String> logs = componentA.getNotificationLogs();

            // Verify each notification
            assertTrue(componentB.verifyNotificationReceived(logs, "user1", "Message 1"));
            assertTrue(componentB.verifyNotificationReceived(logs, "user2", "Message 2"));
            assertTrue(componentB.verifyNotificationReceived(logs, "user1", "Message 3"));
            assertFalse(componentB.verifyNotificationReceived(logs, "user3", "Message 1"));

            // Verify log count
            assertEquals(3, logs.size());
        }
    }
}