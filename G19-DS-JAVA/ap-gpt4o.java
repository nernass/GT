import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ComponentIntegrationTest {

    @InjectMocks
    private ComponentA componentA;

    @InjectMocks
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidInput_AllComponentsSucceed() {
        // Arrange
        String userId = "user123";
        String message = "Hello, World!";
        componentA.sendNotification(userId, message);
        List<String> logs = componentA.getNotificationLogs();

        // Act
        boolean result = componentB.verifyNotificationReceived(logs, userId, message);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testComponentBFailure() {
        // Arrange
        String userId = "user123";
        String message = "Hello, World!";
        componentA.sendNotification(userId, message);
        List<String> logs = componentA.getNotificationLogs();

        // Act
        boolean result = componentB.verifyNotificationReceived(logs, userId, "Wrong Message");

        // Assert
        assertFalse(result);
    }

    @Test
    public void testInvalidInputToComponentA() {
        // Arrange
        String userId = null;
        String message = "Hello, World!";
        boolean sendResult = componentA.sendNotification(userId, message);

        // Act
        List<String> logs = componentA.getNotificationLogs();
        boolean verifyResult = componentB.verifyNotificationReceived(logs, userId, message);

        // Assert
        assertFalse(sendResult);
        assertFalse(verifyResult);
    }
}