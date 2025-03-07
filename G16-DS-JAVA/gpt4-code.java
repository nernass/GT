import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    public void testSendAndVerifyEmail() {
        String emailAddress = "test@example.com";
        String message = "This is a test message";

        // Send email using ComponentA
        boolean emailSent = componentA.sendConfirmationEmail(emailAddress, message);
        assertTrue(emailSent, "Email should be sent successfully");

        // Retrieve email logs from ComponentA
        List<String> emailLogs = componentA.getEmailLogs();
        assertNotNull(emailLogs, "Email logs should not be null");
        assertFalse(emailLogs.isEmpty(), "Email logs should not be empty");

        // Verify email using ComponentB
        boolean emailVerified = componentB.verifyEmailSent(emailLogs, emailAddress, message);
        assertTrue(emailVerified, "Email should be verified successfully");
    }

    @Test
    public void testSendEmailWithNullValues() {
        boolean emailSent = componentA.sendConfirmationEmail(null, null);
        assertFalse(emailSent, "Email should not be sent with null values");

        List<String> emailLogs = componentA.getEmailLogs();
        assertTrue(emailLogs.isEmpty(), "Email logs should be empty");
    }

    @Test
    public void testVerifyEmailWithNullValues() {
        boolean emailVerified = componentB.verifyEmailSent(null, null, null);
        assertFalse(emailVerified, "Email should not be verified with null values");
    }
}