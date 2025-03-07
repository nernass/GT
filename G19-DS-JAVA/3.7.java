import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

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
    @DisplayName("Integration test - Send notification and verify it was received")
    void whenNotificationSentThenVerificationSucceeds() {
        // Given
        String userId = "user123";
        String message = "Test notification message";

        // When
        boolean sent = componentA.sendNotification(userId, message);
        List<String> logs = componentA.getNotificationLogs();
        boolean verified = componentB.verifyNotificationReceived(logs, userId, message);

        // Then
        assertTrue(sent, "Notification should be sent successfully");
        assertTrue(verified, "ComponentB should verify the notification was received");
    }

    @Test
    @DisplayName("Integration test - Multiple notifications for the same user")
    void whenMultipleNotificationsForSameUserThenAllCanBeVerified() {
        // Given
        String userId = "user456";
        String message1 = "First notification";
        String message2 = "Second notification";

        // When
        componentA.sendNotification(userId, message1);
        componentA.sendNotification(userId, message2);
        List<String> logs = componentA.getNotificationLogs();

        // Then
        assertTrue(componentB.verifyNotificationReceived(logs, userId, message1),
                "First notification should be verified");
        assertTrue(componentB.verifyNotificationReceived(logs, userId, message2),
                "Second notification should be verified");
    }

    @Test
    @DisplayName("Integration test - Verification fails for unsent notification")
    void whenNotificationNotSentThenVerificationFails() {
        // Given
        String userId = "user789";
        String sentMessage = "Sent message";
        String unsentMessage = "Unsent message";

        // When
        componentA.sendNotification(userId, sentMessage);
        List<String> logs = componentA.getNotificationLogs();

        // Then
        assertTrue(componentB.verifyNotificationReceived(logs, userId, sentMessage),
                "Sent notification should be verified");
        assertFalse(componentB.verifyNotificationReceived(logs, userId, unsentMessage),
                "Unsent notification should not be verified");
    }

    @ParameterizedTest
    @DisplayName("Integration test - Null parameters handling")
    @MethodSource("provideNullParameters")
    void whenNullParametersThenBothComponentsFailGracefully(String userId, String message) {
        // When
        boolean sent = componentA.sendNotification(userId, message);
        List<String> logs = componentA.getNotificationLogs();
        boolean verified = componentB.verifyNotificationReceived(logs, userId, message);

        // Then
        assertFalse(sent, "Notification should not be sent with null parameters");
        assertFalse(verified, "Verification should fail with null parameters");
    }

    private static Stream<Arguments> provideNullParameters() {
        return Stream.of(
                Arguments.of(null, "Test message"),
                Arguments.of("user123", null),
                Arguments.of(null, null));
    }

    @Test
    @DisplayName("Integration test - Null logs handling")
    void whenNullLogsThenVerificationFails() {
        // Given
        String userId = "user123";
        String message = "Test message";

        // When
        boolean verified = componentB.verifyNotificationReceived(null, userId, message);

        // Then
        assertFalse(verified, "Verification should fail with null logs");
    }

    @Test
    @DisplayName("Integration test - Multiple users with different messages")
    void whenMultipleUsersWithDifferentMessagesThenCorrectVerifications() {
        // Given
        String userId1 = "user111";
        String userId2 = "user222";
        String message1 = "Message for user 1";
        String message2 = "Message for user 2";

        // When
        componentA.sendNotification(userId1, message1);
        componentA.sendNotification(userId2, message2);
        List<String> logs = componentA.getNotificationLogs();

        // Then
        assertTrue(componentB.verifyNotificationReceived(logs, userId1, message1),
                "User 1's notification should be verified");
        assertTrue(componentB.verifyNotificationReceived(logs, userId2, message2),
                "User 2's notification should be verified");
        assertFalse(componentB.verifyNotificationReceived(logs, userId1, message2),
                "User 1 should not have User 2's message");
        assertFalse(componentB.verifyNotificationReceived(logs, userId2, message1),
                "User 2 should not have User 1's message");
    }
}