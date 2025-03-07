import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    private DataSource dataSource;

    @BeforeEach
    public void setUp() {
        // Setup an embedded in-memory database for testing
        EmbeddedDatabase db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql") // Create table schema
                .addScript("test-data.sql") // Populate test data
                .build();

        dataSource = db;

        // Initialize both components with the same data source
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);
    }

    @Test
    public void testDeleteUserByEmailFlow() {
        // Step 1: Verify user exists before deletion
        String testEmail = "test@example.com";
        List<Map<String, Object>> userBeforeDeletion = componentB.getUserByEmail(testEmail);

        // Assert user exists before deletion
        assertFalse(userBeforeDeletion.isEmpty(), "User should exist before deletion");
        assertEquals(testEmail, userBeforeDeletion.get(0).get("email"),
                "Email in retrieved user should match the test email");

        // Step 2: Delete the user
        componentA.deleteUserByEmail(testEmail);

        // Step 3: Verify user no longer exists
        List<Map<String, Object>> userAfterDeletion = componentB.getUserByEmail(testEmail);

        // Assert user no longer exists
        assertTrue(userAfterDeletion.isEmpty(), "User should not exist after deletion");
    }

    @Test
    public void testDeleteNonExistentUser() {
        // Delete a user that doesn't exist
        String nonExistentEmail = "nonexistent@example.com";

        // Verify the user doesn't exist before trying to delete
        List<Map<String, Object>> userBeforeDeletion = componentB.getUserByEmail(nonExistentEmail);
        assertTrue(userBeforeDeletion.isEmpty(), "Non-existent user should not be found");

        // Delete should not throw an exception even if user doesn't exist
        assertDoesNotThrow(() -> {
            componentA.deleteUserByEmail(nonExistentEmail);
        });

        // Verify still no user after deletion attempt
        List<Map<String, Object>> userAfterDeletion = componentB.getUserByEmail(nonExistentEmail);
        assertTrue(userAfterDeletion.isEmpty(), "Non-existent user should still not exist after deletion attempt");
    }

    @Test
    public void testMultipleUserDeletions() {
        // Test deleting multiple users in sequence
        String firstEmail = "user1@example.com";
        String secondEmail = "user2@example.com";

        // Verify both users exist
        assertFalse(componentB.getUserByEmail(firstEmail).isEmpty(), "First user should exist");
        assertFalse(componentB.getUserByEmail(secondEmail).isEmpty(), "Second user should exist");

        // Delete first user
        componentA.deleteUserByEmail(firstEmail);

        // Verify first user is gone but second still exists
        assertTrue(componentB.getUserByEmail(firstEmail).isEmpty(), "First user should be deleted");
        assertFalse(componentB.getUserByEmail(secondEmail).isEmpty(), "Second user should still exist");

        // Delete second user
        componentA.deleteUserByEmail(secondEmail);

        // Verify both users are now gone
        assertTrue(componentB.getUserByEmail(firstEmail).isEmpty(), "First user should be deleted");
        assertTrue(componentB.getUserByEmail(secondEmail).isEmpty(), "Second user should be deleted");
    }
}