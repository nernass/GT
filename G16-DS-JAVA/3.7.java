java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    @DisplayName("Integration test - Email sent and verified successfully")
    void testEmailSendingAndVerification() {
        // Setup test data
        String testEmail = "user@example.com";
        String testMessage = "Your account has been created";
        
        // Test sending email via ComponentA
        boolean sendResult = componentA.sendConfirmationEmail(testEmail, testMessage);
        assertTrue(sendResult, "Email should be sent successfully");
        
        // Get logs from ComponentA
        List<String> logs = componentA.getEmailLogs();
        assertNotNull(logs, "Email logs should not be null");
        assertFalse(logs.isEmpty(), "Email logs should not be empty");
        
        // Verify the email using ComponentB
        boolean verifyResult = componentB.verifyEmailSent(logs, testEmail, testMessage);
        assertTrue(verifyResult, "Email verification should succeed");
    }

    @Test
    @DisplayName("Integration test - Email verification fails with wrong message")
    void testEmailVerificationWithWrongMessage() {
        // Setup test data
        String testEmail = "user@example.com";
        String sentMessage = "Your account has been created";
        String wrongMessage = "Wrong message";
        
        // Send email with correct message
        componentA.sendConfirmationEmail(testEmail, sentMessage);
        List<String> logs = componentA.getEmailLogs();
        
        // Verify with wrong message should fail
        boolean verifyResult = componentB.verifyEmailSent(logs, testEmail, wrongMessage);
        assertFalse(verifyResult, "Email verification should fail with wrong message");
    }

    @Test
    @DisplayName("Integration test - Email verification fails with wrong email")
    void testEmailVerificationWithWrongEmail() {
        // Setup test data
        String sentEmail = "user@example.com";
        String wrongEmail = "wrong@example.com";
        String testMessage = "Your account has been created";
        
        // Send email to correct address
        componentA.sendConfirmationEmail(sentEmail, testMessage);
        List<String> logs = componentA.getEmailLogs();
        
        // Verify with wrong email should fail
        boolean verifyResult = componentB.verifyEmailSent(logs, wrongEmail, testMessage);
        assertFalse(verifyResult, "Email verification should fail with wrong email");
    }

    @Test
    @DisplayName("Integration test - Handle null inputs")
    void testNullInputHandling() {
        // Test sending with null parameters
        assertFalse(componentA.sendConfirmationEmail(null, "Test"), "Should handle null email");
        assertFalse(componentA.sendConfirmationEmail("test@example.com", null), "Should handle null message");
        
        // Setup valid email log
        componentA.sendConfirmationEmail("valid@example.com", "Valid message");
        List<String> logs = componentA.getEmailLogs();
        
        // Test verification with null parameters
        assertFalse(componentB.verifyEmailSent(null, "test@example.com", "Test"), "Should handle null logs");
        assertFalse(componentB.verifyEmailSent(logs, null, "Test"), "Should handle null email");
        assertFalse(componentB.verifyEmailSent(logs, "test@example.com", null), "Should handle null message");
    }

    @Test
    @DisplayName("Integration test - Multiple emails in logs")
    void testMultipleEmailsInLogs() {
        // Send multiple emails
        componentA.sendConfirmationEmail("user1@example.com", "Message for user 1");
        componentA.sendConfirmationEmail("user2@example.com", "Message for user 2");
        componentA.sendConfirmationEmail("user3@example.com", "Message for user 3");
        
        List<String> logs = componentA.getEmailLogs();
        
        // Verify each email
        assertTrue(componentB.verifyEmailSent(logs, "user1@example.com", "Message for user 1"), 
                  "Should find first email");
        assertTrue(componentB.verifyEmailSent(logs, "user2@example.com", "Message for user 2"), 
                  "Should find second email");
        assertTrue(componentB.verifyEmailSent(logs, "user3@example.com", "Message for user 3"), 
                  "Should find third email");
    }
}