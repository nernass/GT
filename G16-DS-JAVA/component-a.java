import java.util.ArrayList;
import java.util.List;

public class ComponentA {

    // Simulate email logs with an in-memory list
    private List<String> emailLogs = new ArrayList<>();

    /**
     * Sends a confirmation email to the user.
     *
     * @param emailAddress The email address of the user.
     * @param message      The confirmation message to send.
     * @return true if the email was "sent" successfully, false otherwise.
     */
    public boolean sendConfirmationEmail(String emailAddress, String message) {
        if (emailAddress == null || message == null) {
            return false;
        }

        // Simulate sending an email by logging it
        String logEntry = "Email sent to: " + emailAddress + " | Message: " + message;
        emailLogs.add(logEntry);
        return true;
    }

    /**
     * Retrieves the email logs for testing purposes.
     *
     * @return The list of email logs.
     */
    public List<String> getEmailLogs() {
        return emailLogs;
    }
}