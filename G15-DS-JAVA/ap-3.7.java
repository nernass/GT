egrationTest.java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    void setUp() {
        // Initialize real components for integration testing
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    void testSuccessfulRegistrationWorkflow() {
        // Test data
        String username = "validUser";
        String email = "valid@example.com";
        String password = "validPassword123";

        // Test the full workflow
        boolean isValid = componentA.validateInput(username, email, password);
        boolean isSaved = false;
        
        if (isValid) {
            isSaved = componentB.saveToDatabase(username, email, password);
        }

        // Assertions
        assertTrue(isValid, "Input should be validated successfully");
        assertTrue(isSaved, "Data should be saved successfully");

        // Verify the data was saved correctly
        String[] savedData = componentB.getLastEntry();
        assertNotNull(savedData, "Saved data should not be null");
        assertEquals(username, savedData[0], "Username should match");
        assertEquals(email, savedData[1], "Email should match");
        assertEquals(password, savedData[2], "Password should match");
    }

    @Test
    void testInvalidInputNotSaved() {
        // Test data with invalid values
        String username = "us"; // Too short
        String email = "invalid-email";
        String password = "short";

        // Test the workflow with invalid input
        boolean isValid = componentA.validateInput(username, email, password);
        boolean isSaved = false;
        
        if (isValid) {
            isSaved = componentB.saveToDatabase(username, email, password);
        }

        // Assertions
        assertFalse(isValid, "Input should fail validation");
        assertFalse(isSaved, "Invalid data should not be saved");
    }

    @ParameterizedTest
    @MethodSource("provideInvalidInputs")
    void testVariousInvalidInputs(String username, String email, String password, String testCase) {
        // Test the validation component
        boolean isValid = componentA.validateInput(username, email, password);
        
        // If by any chance validation passes, check database doesn't save null values
        boolean isSaved = false;
        if (isValid) {
            isSaved = componentB.saveToDatabase(username, email, password);
        }

        // Assertions
        assertFalse(isValid, "Validation should fail for " + testCase);
        assertFalse(isSaved, "Data should not be saved for " + testCase);
    }

    @Test
    void testNullInputsHandling() {
        // Test direct saving of null values to ComponentB
        boolean isSaved = componentB.saveToDatabase(null, null, null);
        
        // Assertions
        assertFalse(isSaved, "Null values should not be saved");
    }

    @Test
    void testMultipleRegistrations() {
        // Register first user
        String username1 = "firstUser";
        String email1 = "first@example.com";
        String password1 = "password123";
        
        boolean isValid1 = componentA.validateInput(username1, email1, password1);
        boolean isSaved1 = false;
        if (isValid1) {
            isSaved1 = componentB.saveToDatabase(username1, email1, password1);
        }
        
        // Register second user
        String username2 = "secondUser";
        String email2 = "second@example.com";
        String password2 = "anotherPassword123";
        
        boolean isValid2 = componentA.validateInput(username2, email2, password2);
        boolean isSaved2 = false;
        if (isValid2) {
            isSaved2 = componentB.saveToDatabase(username2, email2, password2);
        }

        // Assertions
        assertTrue(isValid1 && isSaved1, "First user should be registered");
        assertTrue(isValid2 && isSaved2, "Second user should be registered");
        
        // Verify the last entry is the second user
        String[] lastEntry = componentB.getLastEntry();
        assertEquals(username2, lastEntry[0], "Last entry should be the second user");
    }

    private static Stream<Arguments> provideInvalidInputs() {
        return Stream.of(
            // Invalid username cases
            Arguments.of(null, "valid@example.com", "password123", "null username"),
            Arguments.of("ab", "valid@example.com", "password123", "username too short"),
            
            // Invalid email cases
            Arguments.of("validUser", null, "password123", "null email"),
            Arguments.of("validUser", "invalid-email", "password123", "malformed email"),
            Arguments.of("validUser", "email@", "password123", "incomplete email"),
            
            // Invalid password cases
            Arguments.of("validUser", "valid@example.com", null, "null password"),
            Arguments.of("validUser", "valid@example.com", "short", "password too short")
        );
    }
}