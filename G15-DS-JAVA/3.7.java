.java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

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
        // Given
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";

        // When
        boolean isValid = componentA.validateInput(username, email, password);
        boolean isSaved = false;
        if (isValid) {
            isSaved = componentB.saveToDatabase(username, email, password);
        }

        // Then
        assertTrue(isValid, "Input should be valid");
        assertTrue(isSaved, "Data should be saved to database");

        // Verify data was correctly saved
        String[] lastEntry = componentB.getLastEntry();
        assertNotNull(lastEntry, "Database entry should not be null");
        assertEquals(username, lastEntry[0], "Username should match");
        assertEquals(email, lastEntry[1], "Email should match");
        assertEquals(password, lastEntry[2], "Password should match");
    }

    @ParameterizedTest
    @CsvSource({
        "ab, test@example.com, password123",
        "validuser, invalidemail, password123",
        "validuser, test@example.com, short"
    })
    void testInvalidInputsNotSavedToDatabase(String username, String email, String password) {
        // When
        boolean isValid = componentA.validateInput(username, email, password);
        boolean isSaved = false;
        if (isValid) {
            isSaved = componentB.saveToDatabase(username, email, password);
        }

        // Then
        assertFalse(isValid, "Input should be invalid");
        assertFalse(isSaved, "Invalid data should not be saved");
    }

    @ParameterizedTest
    @NullSource
    void testNullInputsNotSavedToDatabase(String nullInput) {
        // When
        boolean isValid = componentA.validateInput(nullInput, nullInput, nullInput);
        boolean isSaved = false;
        if (isValid) {
            isSaved = componentB.saveToDatabase(nullInput, nullInput, nullInput);
        }

        // Then
        assertFalse(isValid, "Null input should be invalid");
        assertFalse(isSaved, "Null data should not be saved");
    }

    @Test
    void testDirectSaveWithoutValidation() {
        // Given
        String username = "ab"; // Invalid username (too short)
        String email = "test@example.com";
        String password = "password123";

        // When - bypassing validation
        boolean isSaved = componentB.saveToDatabase(username, email, password);

        // Then
        assertTrue(isSaved, "ComponentB should save data regardless of validation");

        // Verify data was saved
        String[] lastEntry = componentB.getLastEntry();
        assertEquals(username, lastEntry[0], "Username should match even if invalid");
    }

    @Test
    void testNullDirectSave() {
        // When - null values passed directly to ComponentB
        boolean isSaved = componentB.saveToDatabase(null, null, null);

        // Then
        assertFalse(isSaved, "ComponentB should not save null data");
    }
}