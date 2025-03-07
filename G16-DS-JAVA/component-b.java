import java.util.List;

public class ComponentB {

    /**
     * Verifies that an email was sent to the specified address by checking the logs.
     *
     * @param emailLogs     The list of email logs to check.
     * @param emailAddress  The email address to verify.
     * @param expectedMessage The expected confirmation message.
     * @return true if the email was found in the logs, false otherwise.
     */
    public boolean verifyEmailSent(List<String> emailLogs, String emailAddress, String expectedMessage) {
        if (emailLogs == null || emailAddress == null || expectedMessage == null) {
            return false;
        }

        // Check each log entry for the expected email
        for (String logEntry : emailLogs) {
            if (logEntry.contains("Email sent to: " + emailAddress) && logEntry.contains("Message: " + expectedMessage)) {
                return true;
            }
        }
        return false;
    }
}