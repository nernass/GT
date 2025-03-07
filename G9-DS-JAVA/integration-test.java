// IntegrationTest.java
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    private static final String LOG_FILE = "test_log.txt";
    private static final String TEST_DATA = "This is a test log entry.";

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() throws IOException {
        // Initialize components
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Create an empty log file
        new File(LOG_FILE).createNewFile();
    }

    @AfterEach
    public void tearDown() {
        // Clean up the log file after each test
        new File(LOG_FILE).delete();
    }

    @Test
    public void testIntegration() throws IOException {
        // Step 1: Append data to the log file using Component A
        componentA.appendToLogFile(LOG_FILE, TEST_DATA);

        // Step 2: Verify the last line of the log file using Component B
        boolean isVerified = componentB.verifyLastLine(LOG_FILE, TEST_DATA);
        assertTrue(isVerified, "The last line of the log file does not match the expected data");
    }
}