import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    void testSuccessfulRegistrationFlow() {
        // Valid input data
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";

        // Test the complete flow
        boolean isValid = componentA.validateInput(username, email, password);
        assertTrue(isValid, "Input validation should pass");

        boolean isSaved = componentB.saveToDatabase(username, email, password);
        assertTrue(isSaved, "Data should be saved successfully");

        // Verify saved data
        String[] savedData = componentB.getLastEntry();
        assertNotNull(savedData, "Saved data should not be null");
        assertEquals(username, savedData[0], "Username should match");
        assertEquals(email, savedData[1], "Email should match");
        assertEquals(password, savedData[2], "Password should match");
    }

    @Test
    void testInvalidInputFlow() {
        // Invalid input data
        String username = "ab"; // too short
        String email = "invalid-email";
        String password = "short";

        boolean isValid = componentA.validateInput(username, email, password);
        assertFalse(isValid, "Input validation should fail");

        boolean isSaved = componentB.saveToDatabase(username, email, password);
        assertTrue(isSaved, "ComponentB should still save invalid data");
    }

    @Test
    void testNullInputFlow() {
        assertFalse(componentA.validateInput(null, null, null),
                "Validation should fail for null inputs");
        assertFalse(componentB.saveToDatabase(null, null, null),
                "Database save should fail for null inputs");
    }

    @Test
    void testBoundaryConditions() {
        String username = "abc"; // minimum length
        String email = "a@b.c"; // minimum valid email
        String password = "12345678"; // minimum length

        assertTrue(componentA.validateInput(username, email, password),
                "Boundary values should be valid");
        assertTrue(componentB.saveToDatabase(username, email, password),
                "Boundary values should be saved");
    }
}