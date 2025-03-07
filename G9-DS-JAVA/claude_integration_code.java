import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class ComponentIntegrationTest {
    private ComponentA componentA;
    private ComponentB componentB;
    private String testLogFile;

    @BeforeEach
    void setUp() throws IOException {
        componentA = new ComponentA();
        componentB = new ComponentB();
        testLogFile = "test_log.txt";
        // Ensure clean file for each test
        Files.deleteIfExists(Path.of(testLogFile));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Path.of(testLogFile));
    }

    @Test
    void testComponentIntegration() throws IOException {
        // Test data
        String testData = "Test log entry";

        // Write data using ComponentA
        componentA.appendToLogFile(testLogFile, testData);

        // Verify data using ComponentB
        assertTrue(componentB.verifyLastLine(testLogFile, testData));
    }

    @Test
    void testMultipleEntriesIntegration() throws IOException {
        String[] testData = { "First entry", "Second entry", "Third entry" };

        // Write multiple entries
        for (String data : testData) {
            componentA.appendToLogFile(testLogFile, data);
        }

        // Verify last entry
        assertTrue(componentB.verifyLastLine(testLogFile, testData[testData.length - 1]));
        assertFalse(componentB.verifyLastLine(testLogFile, testData[0]));
    }

    @Test
    void testEmptyFileHandling() throws IOException {
        // Create empty file
        new File(testLogFile).createNewFile();

        // Verify empty last line
        assertEquals("", componentB.readLastLine(testLogFile));
    }

    @Test
    void testNonExistentFileHandling() {
        String nonExistentFile = "nonexistent.txt";

        // Test exception handling
        assertThrows(IOException.class, () -> {
            componentA.appendToLogFile(nonExistentFile, "Test data");
        });

        assertThrows(IOException.class, () -> {
            componentB.readLastLine(nonExistentFile);
        });
    }
}