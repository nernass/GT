import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ComponentIntegrationTest {

    @TempDir
    Path tempDir;

    private File logFile;
    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    void setUp() {
        logFile = tempDir.resolve("test_log.txt").toFile();
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (logFile.exists()) {
            logFile.delete();
        }
    }

    @Test
    void testComponentIntegration() throws IOException {
        // Test data
        String testData1 = "Test data line 1";
        String testData2 = "Test data line 2";
        String testData3 = "Test data line 3";

        // Use ComponentA to append data to log file
        componentA.appendToLogFile(logFile.getPath(), testData1);
        componentA.appendToLogFile(logFile.getPath(), testData2);
        componentA.appendToLogFile(logFile.getPath(), testData3);

        // Use ComponentB to read and verify the last line
        String lastLine = componentB.readLastLine(logFile.getPath());
        assertEquals(testData3, lastLine, "Last line should match the last appended data");

        boolean verificationResult = componentB.verifyLastLine(logFile.getPath(), testData3);
        assertTrue(verificationResult, "Verification should succeed for the last line");

        // Negative verification test
        verificationResult = componentB.verifyLastLine(logFile.getPath(), testData2);
        assertFalse(verificationResult, "Verification should fail for non-matching data");

        // Verify file content directly
        List<String> fileContent = Files.readAllLines(logFile.toPath());
        assertEquals(3, fileContent.size(), "File should contain exactly three lines");
        assertEquals(testData1, fileContent.get(0), "First line should match first appended data");
        assertEquals(testData2, fileContent.get(1), "Second line should match second appended data");
        assertEquals(testData3, fileContent.get(2), "Third line should match third appended data");
    }

    @Test
    void testEmptyFile() throws IOException {
        // Create empty file
        Files.createFile(logFile.toPath());

        // ComponentB should return empty string for empty file
        String lastLine = componentB.readLastLine(logFile.getPath());
        assertEquals("", lastLine, "Last line of empty file should be empty string");

        // ComponentA should be able to write to empty file
        String testData = "First log entry";
        componentA.appendToLogFile(logFile.getPath(), testData);

        // ComponentB should now read this line
        lastLine = componentB.readLastLine(logFile.getPath());
        assertEquals(testData, lastLine, "Last line should match the appended data");
    }

    @Test
    void testNonExistentFile() {
        // Try to read from non-existent file
        File nonExistentFile = tempDir.resolve("non_existent.txt").toFile();
        assertFalse(nonExistentFile.exists(), "Test file should not exist");

        // ComponentB should throw IOException
        assertThrows(IOException.class, () -> {
            componentB.readLastLine(nonExistentFile.getPath());
        }, "Reading from non-existent file should throw IOException");
    }

    @Test
    void testFilePermissionHandling() throws IOException {
        // Test data
        String testData = "Test data";

        // ComponentA writes data
        componentA.appendToLogFile(logFile.getPath(), testData);

        // Check ComponentB can read it
        assertTrue(componentB.verifyLastLine(logFile.getPath(), testData), "Should verify the written data");

        // Make file read-only if possible (might not work on all systems)
        if (logFile.setReadOnly()) {
            // ComponentA should not be able to append more data
            assertThrows(IOException.class, () -> {
                componentA.appendToLogFile(logFile.getPath(), "More data");
            }, "Writing to read-only file should throw IOException");

            // ComponentB should still be able to read
            String lastLine = componentB.readLastLine(logFile.getPath());
            assertEquals(testData, lastLine, "Last line should still be readable");
        }
    }
}