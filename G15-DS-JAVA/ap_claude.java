import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class ComponentIntegrationTest {

    private ComponentA validator;
    private ComponentB database;

    @BeforeEach
    void setUp() {
        validator = new ComponentA();
        database = new ComponentB();
    }

    /**
     * Simulates the registration workflow by validating and then saving data
     */
    private boolean registerUser(String username, String email, String password) {
        // First validate the input using ComponentA
        boolean isValid = validator.validateInput(username, email, password);

        // Only save to database using ComponentB if validation passed
        if (isValid) {
            return database.saveToDatabase(username, email, password);
        }

        return false;
    }

    @Test
    @DisplayName("Valid user data should be validated and saved successfully")
    void testSuccessfulRegistration() {
        // Given
        String username = "testUser";
        String email = "user@example.com";
        String password = "securePass123";

        // When - Execute the full integration workflow
        boolean registrationSuccess = registerUser(username, email, password);

        // Then
        assertTrue(registrationSuccess, "Registration should succeed for valid inputs");

        // Verify the correct data was saved in the database
        String[] savedData = database.getLastEntry();
        assertNotNull(savedData, "Data should be saved in the database");
        assertEquals(username, savedData[0], "Username should match what was saved");
        assertEquals(email, savedData[1], "Email should match what was saved");
        assertEquals(password, savedData[2], "Password should match what was saved");
    }

    @Test
    @DisplayName("Invalid username should prevent database save")
    void testInvalidUsername() {
        // Given - Username too short
        String username = "ab";
        String email = "user@example.com";
        String password = "securePass123";

        // When
        boolean registrationSuccess = registerUser(username, email, password);

        // Then
        assertFalse(registrationSuccess, "Registration should fail for invalid username");

        // Verify that ComponentA correctly rejected the invalid input
        assertFalse(validator.validateInput(username, email, password),
                "ComponentA should reject invalid username");
    }

    @Test
    @DisplayName("Invalid email should prevent database save")
    void testInvalidEmail() {
        // Given - Email with invalid format
        String username = "validUser";
        String email = "not-an-email";
        String password = "securePass123";

        // When
        boolean registrationSuccess = registerUser(username, email, password);

        // Then
        assertFalse(registrationSuccess, "Registration should fail for invalid email");

        // Verify that ComponentA correctly rejected the invalid input
        assertFalse(validator.validateInput(username, email, password),
                "ComponentA should reject invalid email");
    }

    @Test
    @DisplayName("Invalid password should prevent database save")
    void testInvalidPassword() {
        // Given - Password too short
        String username = "validUser";
        String email = "user@example.com";
        String password = "short";

        // When
        boolean registrationSuccess = registerUser(username, email, password);

        // Then
        assertFalse(registrationSuccess, "Registration should fail for invalid password");

        // Verify that ComponentA correctly rejected the invalid input
        assertFalse(validator.validateInput(username, email, password),
                "ComponentA should reject invalid password");
    }

    @Test
    @DisplayName("Null inputs should prevent database save")
    void testNullInputs() {
        // When
        boolean registrationSuccess = registerUser(null, null, null);

        // Then
        assertFalse(registrationSuccess, "Registration should fail for null inputs");

        // Verify individual components handle nulls correctly
        assertFalse(validator.validateInput(null, null, null),
                "ComponentA should reject null inputs");
        assertFalse(database.saveToDatabase(null, null, null),
                "ComponentB should reject null inputs directly");
    }

    @Test
    @DisplayName("Boundary values should pass validation and be saved")
    void testBoundaryValues() {
        // Given - Minimum valid values
        String username = "abc"; // Exactly 3 chars (minimum)
        String email = "a@b.c"; // Simplest valid email
        String password = "12345678"; // Exactly 8 chars (minimum)

        // When
        boolean registrationSuccess = registerUser(username, email, password);

        // Then
        assertTrue(registrationSuccess, "Registration should succeed with minimum valid values");

        // Verify data is correctly saved
        String[] savedData = database.getLastEntry();
        assertNotNull(savedData, "Data with boundary values should be saved");
        assertEquals(username, savedData[0]);
        assertEquals(email, savedData[1]);
        assertEquals(password, savedData[2]);
    }

    @Test
    @DisplayName("Multiple registrations should maintain data integrity")
    void testMultipleRegistrations() {
        // Register first user
        assertTrue(registerUser("firstUser", "first@example.com", "password123"));

        // Register second user
        assertTrue(registerUser("secondUser", "second@example.com", "different456"));

        // Verify the last entry is the second user
        String[] lastEntry = database.getLastEntry();
        assertNotNull(lastEntry);
        assertEquals("secondUser", lastEntry[0]);
        assertEquals("second@example.com", lastEntry[1]);
        assertEquals("different456", lastEntry[2]);
    }

    @Test
    @DisplayName("Special characters in valid inputs should be processed correctly")
    void testSpecialCharacters() {
        // Given - Valid inputs with special characters
        String username = "user_123";
        String email = "user.name+tag@example.co.uk";
        String password = "P@$$w0rd!";

        // When
        boolean registrationSuccess = registerUser(username, email, password);

        // Then
        assertTrue(registrationSuccess, "Registration should handle special characters in valid inputs");

        // Verify data was saved correctly
        String[] savedData = database.getLastEntry();
        assertEquals(email, savedData[1], "Email with special characters should be preserved");
        assertEquals(password, savedData[2], "Password with special characters should be preserved");
    }
}