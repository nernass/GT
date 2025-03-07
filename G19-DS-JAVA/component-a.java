import java.util.ArrayList;
import java.util.List;

public class ComponentA {

    // Simulate notification logs with an in-memory list
    private List<String> notificationLogs = new ArrayList<>();

    /**
     * Sends a notification to the user.
     *
     * @param userId      The ID of the user.
     * @param message     The notification message to send.
     * @return true if the notification was "sent" successfully, false otherwise.
     */
    public boolean sendNotification(String userId, String message) {
        if (userId == null || message == null) {
            return false;
        }

        // Simulate sending a notification by logging it
        String logEntry = "Notification sent to user: " + userId + " | Message: " + message;
        notificationLogs.add(logEntry);
        return true;
    }

    /**
     * Retrieves the notification logs for testing purposes.
     *
     * @return The list of notification logs.
     */
    public List<String> getNotificationLogs() {
        return notificationLogs;
    }
}