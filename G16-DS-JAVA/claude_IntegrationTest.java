import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    void testSuccessfulEmailSendAndVerification() {
        String email = "test@example.com";
        String message = "Welcome to our service!";

        // Send email using ComponentA
        boolean sendResult = componentA.sendConfirmationEmail(email, message);
        assertTrue(sendResult, "Email should be sent successfully");

        // Verify email using ComponentB
        boolean verifyResult = componentB.verifyEmailSent(componentA.getEmailLogs(), email, message);
        assertTrue(verifyResult, "Email should be found in logs");
    }

    @Test
    void testNullInputHandling() {
        // Test null email address
        assertFalse(componentA.sendConfirmationEmail(null, "message"));
        assertFalse(componentB.verifyEmailSent(componentA.getEmailLogs(), null, "message"));

        // Test null message
        assertFalse(componentA.sendConfirmationEmail("test@example.com", null));
        assertFalse(componentB.verifyEmailSent(componentA.getEmailLogs(), "test@example.com", null));

        // Test null logs
        assertFalse(componentB.verifyEmailSent(null, "test@example.com", "message"));
    }

    @Test
    void testNonExistentEmailVerification() {
        String email = "test@example.com";
        String message = "Welcome!";

        // Send email
        componentA.sendConfirmationEmail(email, message);

        // Verify with different email/message
        assertFalse(componentB.verifyEmailSent(componentA.getEmailLogs(), "other@example.com", message));
        assertFalse(componentB.verifyEmailSent(componentA.getEmailLogs(), email, "Different message"));
    }

    @Test
    void testMultipleEmailsHandling() {
        // Send multiple emails
        componentA.sendConfirmationEmail("user1@example.com", "Message 1");
        componentA.sendConfirmationEmail("user2@example.com", "Message 2");
        componentA.sendConfirmationEmail("user3@example.com", "Message 3");

        // Verify each email
        assertTrue(componentB.verifyEmailSent(componentA.getEmailLogs(), "user1@example.com", "Message 1"));
        assertTrue(componentB.verifyEmailSent(componentA.getEmailLogs(), "user2@example.com", "Message 2"));
        assertTrue(componentB.verifyEmailSent(componentA.getEmailLogs(), "user3@example.com", "Message 3"));
    }
}