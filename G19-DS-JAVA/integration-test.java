import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    @Test
    public void testNotificationSendingAndVerification() {
        // Create instances of ComponentA and ComponentB
        ComponentA notificationSender = new ComponentA();
        ComponentB notificationVerifier = new ComponentB();

        // Test data
        String userId = "user123";
        String message = "Your order has been shipped.";

        // Send a notification using ComponentA
        boolean isNotificationSent = notificationSender.sendNotification(userId, message);
        assertTrue(isNotificationSent, "Notification sending failed");

        // Verify the notification was received by checking the logs using ComponentB
        List<String> notificationLogs = notificationSender.getNotificationLogs();
        boolean isNotificationVerified = notificationVerifier.verifyNotificationReceived(notificationLogs, userId, message);
        assertTrue(isNotificationVerified, "Notification verification failed");
    }
}