import java.util.List;

public class ComponentB {

    /**
     * Verifies that a notification was received by the specified user by checking the logs.
     *
     * @param notificationLogs The list of notification logs to check.
     * @param userId           The ID of the user to verify.
     * @param expectedMessage  The expected notification message.
     * @return true if the notification was found in the logs, false otherwise.
     */
    public boolean verifyNotificationReceived(List<String> notificationLogs, String userId, String expectedMessage) {
        if (notificationLogs == null || userId == null || expectedMessage == null) {
            return false;
        }

        // Check each log entry for the expected notification
        for (String logEntry : notificationLogs) {
            if (logEntry.contains("Notification sent to user: " + userId) && logEntry.contains("Message: " + expectedMessage)) {
                return true;
            }
        }
        return false;
    }
}