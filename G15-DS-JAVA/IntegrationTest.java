import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    @Test
    public void testIntegration() {
        // Create instances of ComponentA and ComponentB
        ComponentA validator = new ComponentA();
        ComponentB dataSaver = new ComponentB();

        // Test input
        String username = "testuser";
        String email = "testuser@example.com";
        String password = "password123";

        // Validate input using ComponentA
        boolean isValid = validator.validateInput(username, email, password);
        assertTrue(isValid, "Input validation failed");

        // Save validated data to the database using ComponentB
        boolean isSaved = dataSaver.saveToDatabase(username, email, password);
        assertTrue(isSaved, "Data saving failed");

        // Verify the data saved in the database
        String[] lastEntry = dataSaver.getLastEntry();
        assertNotNull(lastEntry, "No data was saved to the database");
        assertEquals(username, lastEntry[0], "Username does not match");
        assertEquals(email, lastEntry[1], "Email does not match");
        assertEquals(password, lastEntry[2], "Password does not match");
    }
}