import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ComponentIntegrationTest {

    private EmbeddedDatabase dataSource;
    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    void setUp() {
        dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .addScript("test-data.sql")
                .build();

        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);
    }

    @Test
    void testUserDeletionFlow() {
        // Given
        String testEmail = "test@example.com";

        // When - First verify user exists
        List<Map<String, Object>> userBefore = componentB.getUserByEmail(testEmail);
        assertFalse(userBefore.isEmpty(), "User should exist before deletion");

        // Then delete the user
        componentA.deleteUserByEmail(testEmail);

        // Verify user no longer exists
        List<Map<String, Object>> userAfter = componentB.getUserByEmail(testEmail);
        assertTrue(userAfter.isEmpty(), "User should not exist after deletion");
    }

    @Test
    void testDeleteNonExistentUser() {
        // Given
        String nonExistentEmail = "nonexistent@example.com";

        // When - Verify user doesn't exist initially
        List<Map<String, Object>> userBefore = componentB.getUserByEmail(nonExistentEmail);
        assertTrue(userBefore.isEmpty(), "User should not exist initially");

        // Then - Delete should not throw exception
        assertDoesNotThrow(() -> componentA.deleteUserByEmail(nonExistentEmail));

        // Verify still no user exists
        List<Map<String, Object>> userAfter = componentB.getUserByEmail(nonExistentEmail);
        assertTrue(userAfter.isEmpty(), "User should still not exist after deletion attempt");
    }

    @Test
    void testInvalidEmailFormat() {
        // Given
        String invalidEmail = "invalid-email";

        // When/Then - Both components should handle invalid email gracefully
        assertDoesNotThrow(() -> componentA.deleteUserByEmail(invalidEmail));
        assertDoesNotThrow(() -> componentB.getUserByEmail(invalidEmail));
    }

    @AfterEach
    void tearDown() {
        dataSource.shutdown();
    }
}