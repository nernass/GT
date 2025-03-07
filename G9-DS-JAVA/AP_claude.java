import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ComponentABIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    private String tempLogFilePath;

    @BeforeEach
    public void setup() throws IOException {
        // Create component instances
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Create temporary file for testing
        Path tempFile = Files.createTempFile("test_log", ".txt");
        tempLogFilePath = tempFile.toString();
    }

    @AfterEach
    public void cleanup() {
        // Delete the temporary log file after each test
        new File(tempLogFilePath).delete();
    }

    @Test
    public void testSuccessfulIntegration() throws IOException {
        // Test data
        String testData = "Test log entry";

        // ComponentA writes to log file
        componentA.appendToLogFile(tempLogFilePath, testData);

        // ComponentB reads and verifies
        String lastLine = componentB.readLastLine(tempLogFilePath);

        // Verify integration works correctly
        Assertions.assertEquals(testData, lastLine);
        Assertions.assertTrue(componentB.verifyLastLine(tempLogFilePath, testData));
    }

    @Test
    public void testVerificationFailure() throws IOException {
        // Test data
        String testData = "Test log entry";
        String incorrectData = "Different data";

        // ComponentA writes to log file
        componentA.appendToLogFile(tempLogFilePath, testData);

        // ComponentB verifies with incorrect data
        boolean result = componentB.verifyLastLine(tempLogFilePath, incorrectData);

        // Assert verification fails
        Assertions.assertFalse(result);
    }

    @Test
    public void testMultipleWrites() throws IOException {
        // Test multiple entries
        String firstData = "First entry";
        String secondData = "Second entry";
        String thirdData = "Third entry";

        // ComponentA writes multiple entries
        componentA.appendToLogFile(tempLogFilePath, firstData);
        componentA.appendToLogFile(tempLogFilePath, secondData);
        componentA.appendToLogFile(tempLogFilePath, thirdData);

        // ComponentB reads last line
        String lastLine = componentB.readLastLine(tempLogFilePath);

        // Verify only the last entry is returned
        Assertions.assertEquals(thirdData, lastLine);
        Assertions.assertTrue(componentB.verifyLastLine(tempLogFilePath, thirdData));
        Assertions.assertFalse(componentB.verifyLastLine(tempLogFilePath, firstData));
    }

    @Test
    public void testEmptyData() throws IOException {
        // Test with empty data
        String emptyData = "";

        // ComponentA writes empty data
        componentA.appendToLogFile(tempLogFilePath, emptyData);

        // ComponentB reads and verifies
        String lastLine = componentB.readLastLine(tempLogFilePath);

        // Verify empty data is handled correctly
        Assertions.assertEquals(emptyData, lastLine);
        Assertions.assertTrue(componentB.verifyLastLine(tempLogFilePath, emptyData));
    }

    @Test
    public void testReadFromEmptyFile() throws IOException {
        // Without writing anything, the file is empty

        // ComponentB reads from empty file
        String lastLine = componentB.readLastLine(tempLogFilePath);

        // Verify empty string is returned
        Assertions.assertEquals("", lastLine);
    }

    @Test
    public void testFileNotFound() {
        // Non-existent file path
        String nonExistentFile = "non_existent_file.log";

        // Verify IOException is thrown for both components
        Assertions.assertThrows(IOException.class, () -> componentB.readLastLine(nonExistentFile));
        Assertions.assertThrows(IOException.class, () -> componentB.verifyLastLine(nonExistentFile, "any data"));
        Assertions.assertThrows(IOException.class, () -> componentA.appendToLogFile(nonExistentFile, "any data"));
    }

    @Test
    public void testSpecialCharacters() throws IOException {
        // Test data with special characters
        String specialData = "Special chars: !@#$%^&*()_+{}|:\"<>?[];',./\\";

        // ComponentA writes special data
        componentA.appendToLogFile(tempLogFilePath, specialData);

        // ComponentB reads and verifies
        String lastLine = componentB.readLastLine(tempLogFilePath);

        // Verify special characters are handled correctly
        Assertions.assertEquals(specialData, lastLine);
        Assertions.assertTrue(componentB.verifyLastLine(tempLogFilePath, specialData));
    }
}