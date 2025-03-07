import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setup() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    public void testSuccessfulEmailSendAndVerification() {
        // Test data
        String email = "test@example.com";
        String message = "Your order has been confirmed";

        // ComponentA sends email
        boolean emailSent = componentA.sendConfirmationEmail(email, message);

        // Get logs from ComponentA
        List<String> logs = componentA.getEmailLogs();

        // ComponentB verifies email
        boolean emailVerified = componentB.verifyEmailSent(logs, email, message);

        // Assert both operations were successful
        assertTrue(emailSent, "Email should be sent successfully");
        assertTrue(emailVerified, "Email should be verified successfully");
    }

    @Test
    public void testNullParametersHandling() {
        // Test with null email
        assertFalse(componentA.sendConfirmationEmail(null, "Test message"));

        // Test with null message
        assertFalse(componentA.sendConfirmationEmail("test@example.com", null));

        // Send a valid email first to get non-empty logs
        componentA.sendConfirmationEmail("valid@example.com", "Valid message");

        // Test ComponentB with null logs
        assertFalse(componentB.verifyEmailSent(null, "test@example.com", "Test message"));

        // Test ComponentB with null email address
        assertFalse(componentB.verifyEmailSent(componentA.getEmailLogs(), null, "Test message"));

        // Test ComponentB with null expected message
        assertFalse(componentB.verifyEmailSent(componentA.getEmailLogs(), "test@example.com", null));
    }

    @Test
    public void testEdgeCases() {
        // Empty email and message
        assertTrue(componentA.sendConfirmationEmail("", ""), "Should accept empty strings");

        // Send email with empty values
        componentA.sendConfirmationEmail("", "");
        List<String> logs = componentA.getEmailLogs();

        // ComponentB should still find the empty email and message
        assertTrue(componentB.verifyEmailSent(logs, "", ""), "Should verify empty strings");

        // Test with special characters
        String specialEmail = "test+special@example.com";
        String specialMessage = "Your order #12345 has been confirmed! 100% satisfaction guaranteed.";

        componentA.sendConfirmationEmail(specialEmail, specialMessage);
        assertTrue(componentB.verifyEmailSent(componentA.getEmailLogs(), specialEmail, specialMessage),
                "Should handle special characters properly");
    }

    @Test
    public void testMultipleEmailsScenario() {
        // Send multiple emails
        componentA.sendConfirmationEmail("user1@example.com", "Message 1");
        componentA.sendConfirmationEmail("user2@example.com", "Message 2");
        componentA.sendConfirmationEmail("user3@example.com", "Message 3");

        // Get logs
        List<String> logs = componentA.getEmailLogs();

        // Verify all emails
        assertTrue(componentB.verifyEmailSent(logs, "user1@example.com", "Message 1"));
        assertTrue(componentB.verifyEmailSent(logs, "user2@example.com", "Message 2"));
        assertTrue(componentB.verifyEmailSent(logs, "user3@example.com", "Message 3"));

        // Verify non-existent email
        assertFalse(componentB.verifyEmailSent(logs, "nonexistent@example.com", "Some message"));

        // Verify wrong message for existing email
        assertFalse(componentB.verifyEmailSent(logs, "user1@example.com", "Wrong message"));
    }

    @Test
    public void testPartialMatchScenario() {
        // Send an email
        componentA.sendConfirmationEmail("partial@example.com", "This is a unique message");

        // Get logs
        List<String> logs = componentA.getEmailLogs();

        // ComponentB should match exact email and message
        assertTrue(componentB.verifyEmailSent(logs, "partial@example.com", "This is a unique message"));

        // ComponentB should not match similar but different message
        assertFalse(componentB.verifyEmailSent(logs, "partial@example.com", "This is a similar message"));

        // ComponentB should not match similar but different email
        assertFalse(componentB.verifyEmailSent(logs, "different@example.com", "This is a unique message"));
    }

    @Test
    public void testLargeVolumeEmailProcessing() {
        // Test sending and verifying multiple emails in sequence
        int emailCount = 100;

        for (int i = 0; i < emailCount; i++) {
            String email = "user" + i + "@example.com";
            String message = "Confirmation message #" + i;

            assertTrue(componentA.sendConfirmationEmail(email, message));
        }

        List<String> logs = componentA.getEmailLogs();
        assertEquals(emailCount, logs.size(), "All emails should be logged");

        // Verify random subset of emails
        for (int i = 0; i < 10; i++) {
            int index = i * 10; // Check every 10th email
            String email = "user" + index + "@example.com";
            String message = "Confirmation message #" + index;

            assertTrue(componentB.verifyEmailSent(logs, email, message),
                    "Email " + index + " should be verified successfully");
        }
    }
}