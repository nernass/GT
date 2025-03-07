import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

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
    @DisplayName("Integration test - Valid email flow")
    void validEmailFlowTest() {
        // Test data
        String email = "user@example.com";
        String message = "Your order has been confirmed!";

        // Send email using ComponentA
        boolean emailSent = componentA.sendConfirmationEmail(email, message);

        // Verify email was sent
        assertTrue(emailSent, "Email should be sent successfully");

        // Get logs from ComponentA
        List<String> logs = componentA.getEmailLogs();

        // Use ComponentB to verify email in logs
        boolean emailVerified = componentB.verifyEmailSent(logs, email, message);

        // Assert that ComponentB correctly verified the email
        assertTrue(emailVerified, "ComponentB should verify email was sent");
    }

    @Test
    @DisplayName("Integration test - Multiple emails")
    void multipleEmailsTest() {
        // Send multiple emails
        componentA.sendConfirmationEmail("user1@example.com", "Message 1");
        componentA.sendConfirmationEmail("user2@example.com", "Message 2");
        componentA.sendConfirmationEmail("user3@example.com", "Message 3");

        // Get logs
        List<String> logs = componentA.getEmailLogs();

        // Verify each email using ComponentB
        assertTrue(componentB.verifyEmailSent(logs, "user1@example.com", "Message 1"));
        assertTrue(componentB.verifyEmailSent(logs, "user2@example.com", "Message 2"));
        assertTrue(componentB.verifyEmailSent(logs, "user3@example.com", "Message 3"));

        // Verify non-existent email
        assertFalse(componentB.verifyEmailSent(logs, "nonexistent@example.com", "Message"));
    }

    @ParameterizedTest
    @DisplayName("Integration test - Invalid inputs")
    @NullSource
    @ValueSource(strings = { "", " " })
    void invalidInputsTest(String invalidInput) {
        // Test with invalid email
        assertFalse(componentA.sendConfirmationEmail(invalidInput, "Valid message"));

        // Test with invalid message
        assertFalse(componentA.sendConfirmationEmail("valid@example.com", invalidInput));

        // Get logs
        List<String> logs = componentA.getEmailLogs();

        // Verify ComponentB handles invalid inputs correctly
        assertFalse(componentB.verifyEmailSent(logs, invalidInput, "Valid message"));
        assertFalse(componentB.verifyEmailSent(logs, "valid@example.com", invalidInput));
        assertFalse(componentB.verifyEmailSent(null, "valid@example.com", "Valid message"));
    }

    @Test
    @DisplayName("Integration test - Edge case: Message containing special verification text")
    void specialMessageContentTest() {
        // Send email with message containing the verification string
        String specialMessage = "This message contains Email sent to: text";
        componentA.sendConfirmationEmail("user@example.com", specialMessage);

        // Get logs
        List<String> logs = componentA.getEmailLogs();

        // Verify ComponentB correctly handles this edge case
        assertTrue(componentB.verifyEmailSent(logs, "user@example.com", specialMessage));

        // Make sure we don't get false positives
        assertFalse(componentB.verifyEmailSent(logs, "Email sent to:", "text"));
    }

    @Test
    @DisplayName("Integration test - End-to-end flow with real world scenario")
    void endToEndFlowTest() {
        // Simulate order confirmation email flow
        String customerEmail = "customer@example.com";
        String orderNumber = "ORD-12345";
        String message = "Thank you for your order #" + orderNumber + ". Your items will ship soon!";

        // ComponentA sends the email
        boolean sent = componentA.sendConfirmationEmail(customerEmail, message);
        assertTrue(sent, "Email should be sent successfully");

        // ComponentB verifies the email was sent (simulating an audit check)
        boolean verified = componentB.verifyEmailSent(componentA.getEmailLogs(), customerEmail, message);
        assertTrue(verified, "Email verification should succeed");

        // Additional verification - check if log size is correct
        assertEquals(1, componentA.getEmailLogs().size(), "Should have one email log entry");
    }
}