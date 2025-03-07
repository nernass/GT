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
    public void testSuccessfulNotificationFlow() {
        // Arrange
        String userId = "user123";
        String message = "Hello, this is a test notification";

        // Act
        boolean sendResult = componentA.sendNotification(userId, message);
        List<String> logs = componentA.getNotificationLogs();
        boolean verifyResult = componentB.verifyNotificationReceived(logs, userId, message);

        // Assert
        assertTrue(sendResult, "Notification should be sent successfully");
        assertTrue(verifyResult, "ComponentB should verify the notification was received");
    }

    @Test
    public void testNotificationNotFound() {
        // Arrange
        String userId = "user123";
        String sentMessage = "Hello, this is a test notification";
        String differentMessage = "This message was not sent";

        // Act
        componentA.sendNotification(userId, sentMessage);
        List<String> logs = componentA.getNotificationLogs();
        boolean verifyResult = componentB.verifyNotificationReceived(logs, userId, differentMessage);

        // Assert
        assertFalse(verifyResult, "ComponentB should not find a notification that wasn't sent");
    }

    @Test
    public void testWrongUserIdNotFound() {
        // Arrange
        String sentUserId = "user123";
        String wrongUserId = "user456";
        String message = "Test notification";

        // Act
        componentA.sendNotification(sentUserId, message);
        List<String> logs = componentA.getNotificationLogs();
        boolean verifyResult = componentB.verifyNotificationReceived(logs, wrongUserId, message);

        // Assert
        assertFalse(verifyResult, "ComponentB should not find a notification for the wrong user");
    }

    @Test
    public void testNullParameters() {
        // Arrange
        String userId = "user123";
        String message = "Test message";

        // Act & Assert - Test ComponentA with nulls
        assertFalse(componentA.sendNotification(null, message), "Should fail with null userId");
        assertFalse(componentA.sendNotification(userId, null), "Should fail with null message");

        // Send a valid notification for verification tests
        componentA.sendNotification(userId, message);
        List<String> logs = componentA.getNotificationLogs();

        // Act & Assert - Test ComponentB with nulls
        assertFalse(componentB.verifyNotificationReceived(null, userId, message),
                "Should fail with null logs");
        assertFalse(componentB.verifyNotificationReceived(logs, null, message),
                "Should fail with null userId in verification");
        assertFalse(componentB.verifyNotificationReceived(logs, userId, null),
                "Should fail with null message in verification");
    }

    @Test
    public void testMultipleNotifications() {
        // Arrange
        String userId1 = "user123";
        String userId2 = "user456";
        String message1 = "First notification";
        String message2 = "Second notification";

        // Act
        componentA.sendNotification(userId1, message1);
        componentA.sendNotification(userId2, message2);
        List<String> logs = componentA.getNotificationLogs();

        // Assert
        assertTrue(componentB.verifyNotificationReceived(logs, userId1, message1),
                "Should find first notification");
        assertTrue(componentB.verifyNotificationReceived(logs, userId2, message2),
                "Should find second notification");
    }

    @Test
    public void testSpecialCharactersInMessage() {
        // Arrange
        String userId = "user123";
        String specialMessage = "Special chars: !@#$%^&*()_+{}|:<>?[]\\;',./";

        // Act
        boolean sendResult = componentA.sendNotification(userId, specialMessage);
        List<String> logs = componentA.getNotificationLogs();
        boolean verifyResult = componentB.verifyNotificationReceived(logs, userId, specialMessage);

        // Assert
        assertTrue(sendResult, "Should send notification with special characters");
        assertTrue(verifyResult, "Should verify notification with special characters");
    }
}