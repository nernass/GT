import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ComponentIntegrationTest {

    @Mock
    private ComponentB componentB;

    @InjectMocks
    private ComponentA componentA;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSuccessPath() {
        String emailAddress = "test@example.com";
        String message = "Confirmation message";

        componentA.sendConfirmationEmail(emailAddress, message);
        List<String> emailLogs = componentA.getEmailLogs();

        when(componentB.verifyEmailSent(emailLogs, emailAddress, message)).thenReturn(true);

        assertTrue(componentB.verifyEmailSent(emailLogs, emailAddress, message));
    }

    @Test
    public void testPartialFailure() {
        String emailAddress = "test@example.com";
        String message = "Confirmation message";

        componentA.sendConfirmationEmail(emailAddress, message);
        List<String> emailLogs = componentA.getEmailLogs();

        when(componentB.verifyEmailSent(emailLogs, emailAddress, message)).thenReturn(false);

        assertFalse(componentB.verifyEmailSent(emailLogs, emailAddress, message));
    }

    @Test
    public void testEdgeCaseNullInputs() {
        assertFalse(componentA.sendConfirmationEmail(null, "message"));
        assertFalse(componentA.sendConfirmationEmail("test@example.com", null));
        assertFalse(componentB.verifyEmailSent(null, "test@example.com", "message"));
        assertFalse(componentB.verifyEmailSent(new ArrayList<>(), null, "message"));
        assertFalse(componentB.verifyEmailSent(new ArrayList<>(), "test@example.com", null));
    }
}