import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    @Test
    public void testEmailSendingAndVerification() {
        // Create instances of ComponentA and ComponentB
        ComponentA emailSender = new ComponentA();
        ComponentB emailVerifier = new ComponentB();

        // Test data
        String emailAddress = "user@example.com";
        String message = "Please confirm your registration.";

        // Send a confirmation email using ComponentA
        boolean isEmailSent = emailSender.sendConfirmationEmail(emailAddress, message);
        assertTrue(isEmailSent, "Email sending failed");

        // Verify the email was sent by checking the logs using ComponentB
        List<String> emailLogs = emailSender.getEmailLogs();
        boolean isEmailVerified = emailVerifier.verifyEmailSent(emailLogs, emailAddress, message);
        assertTrue(isEmailVerified, "Email verification failed");
    }
}