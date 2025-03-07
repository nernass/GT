import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ComponentABIntegrationTest {

    private EmbeddedDatabase dataSource;
    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        // Set up in-memory database for testing
        dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .addScript("test-data.sql")
                .build();

        // Initialize components with the same database connection
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);
    }

    @AfterEach
    public void tearDown() {
        dataSource.shutdown();
    }

    @Test
    public void testDeleteUserAndVerifyDeletion() {
        // Scenario 1: Find user with ComponentB, delete with ComponentA, verify
        // deletion with ComponentB
        String testEmail = "test@example.com";

        // First verify user exists using ComponentB
        List<Map<String, Object>> userBefore = componentB.getUserByEmail(testEmail);
        assertFalse(userBefore.isEmpty(), "User should exist before deletion");
        assertEquals(testEmail, userBefore.get(0).get("email"), "Retrieved user should have the correct email");

        // Delete user using ComponentA
        componentA.deleteUserByEmail(testEmail);

        // Verify user no longer exists using ComponentB
        List<Map<String, Object>> userAfter = componentB.getUserByEmail(testEmail);
        assertTrue(userAfter.isEmpty(), "User should not exist after deletion");
    }

    @Test
    public void testDeleteNonExistentUser() {
        // Scenario 2: Attempt to delete non-existent user and verify no errors occur
        String nonExistentEmail = "nonexistent@example.com";

        // Verify user doesn't exist first
        List<Map<String, Object>> userBefore = componentB.getUserByEmail(nonExistentEmail);
        assertTrue(userBefore.isEmpty(), "Non-existent user should not be found");

        // Attempt to delete non-existent user - should not throw exceptions
        assertDoesNotThrow(() -> componentA.deleteUserByEmail(nonExistentEmail),
                "Deleting non-existent user should not cause exceptions");

        // Verify state remains consistent
        List<Map<String, Object>> userAfter = componentB.getUserByEmail(nonExistentEmail);
        assertTrue(userAfter.isEmpty(), "User should still not exist after attempted deletion");
    }

    @Test
    public void testDeleteOneUserPreservesOthers() {
        // Scenario 3: Delete one user and verify other users remain intact
        String user1Email = "user1@example.com";
        String user2Email = "user2@example.com";

        // Verify both users exist initially
        assertFalse(componentB.getUserByEmail(user1Email).isEmpty(), "User1 should exist");
        assertFalse(componentB.getUserByEmail(user2Email).isEmpty(), "User2 should exist");

        // Delete only user1
        componentA.deleteUserByEmail(user1Email);

        // Verify user1 was deleted but user2 remains
        assertTrue(componentB.getUserByEmail(user1Email).isEmpty(), "User1 should be deleted");
        assertFalse(componentB.getUserByEmail(user2Email).isEmpty(), "User2 should still exist");
    }
}