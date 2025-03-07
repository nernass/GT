import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    public void testValidInputAndSaveToDatabase() {
        String username = "validUser";
        String email = "valid@example.com";
        String password = "validPass123";

        boolean isValid = componentA.validateInput(username, email, password);
        assertTrue(isValid, "Input should be valid");

        boolean isSaved = componentB.saveToDatabase(username, email, password);
        assertTrue(isSaved, "Data should be saved successfully");

        String[] lastEntry = componentB.getLastEntry();
        assertNotNull(lastEntry, "Last entry should not be null");
        assertArrayEquals(new String[]{username, email, password}, lastEntry, "Last entry should match the saved data");
    }

    @Test
    public void testInvalidInputAndSaveToDatabase() {
        String username = "us";
        String email = "invalid-email";
        String password = "short";

        boolean isValid = componentA.validateInput(username, email, password);
        assertFalse(isValid, "Input should be invalid");

        boolean isSaved = componentB.saveToDatabase(username, email, password);
        assertFalse(isSaved, "Data should not be saved");

        String[] lastEntry = componentB.getLastEntry();
        assertNull(lastEntry, "Last entry should be null as no valid data was saved");
    }
}