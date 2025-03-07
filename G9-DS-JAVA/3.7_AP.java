import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @TempDir
    Path tempDir;

    private File logFile;

    @BeforeEach
    void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
        logFile = tempDir.resolve("test_log.txt").toFile();
    }

    @AfterEach
    void tearDown() {
        if (logFile.exists()) {
            logFile.delete();
        }
    }

    @Test
    void testComponentsIntegration_SuccessPath() throws IOException {
        // Test data
        String testData1 = "First line of test data";
        String testData2 = "Second line of test data";
        String testData3 = "Third line of test data";

        // Use ComponentA to write data to log
        componentA.appendToLogFile(logFile.getAbsolutePath(), testData1);
        componentA.appendToLogFile(logFile.getAbsolutePath(), testData2);
        componentA.appendToLogFile(logFile.getAbsolutePath(), testData3);

        // Use ComponentB to read and verify the log
        String lastLine = componentB.readLastLine(logFile.getAbsolutePath());
        boolean verificationResult = componentB.verifyLastLine(logFile.getAbsolutePath(), testData3);

        // Assertions
        assertEquals(testData3, lastLine, "Last line should match the last data appended");
        assertTrue(verificationResult, "Verification of last line should be successful");

        // Verify all content was written correctly
        List<String> allLines = Files.readAllLines(logFile.toPath());
        assertEquals(3, allLines.size(), "Log file should contain 3 lines");
        assertEquals(testData1, allLines.get(0), "First line should match first data appended");
        assertEquals(testData2, allLines.get(1), "Second line should match second data appended");
        assertEquals(testData3, allLines.get(2), "Third line should match third data appended");
    }

    @Test
    void testComponentsIntegration_EmptyFile() throws IOException {
        // Create empty file
        assertTrue(logFile.createNewFile());

        // Verify ComponentB handles empty file correctly
        String lastLine = componentB.readLastLine(logFile.getAbsolutePath());
        assertEquals("", lastLine, "Last line of empty file should be empty string");

        // Append data and verify integration
        String testData = "New test data";
        componentA.appendToLogFile(logFile.getAbsolutePath(), testData);

        boolean verificationResult = componentB.verifyLastLine(logFile.getAbsolutePath(), testData);
        assertTrue(verificationResult, "Verification after appending to empty file should succeed");
    }

    @Test
    void testComponentsIntegration_VerificationMismatch() throws IOException {
        // Test data
        String testData = "Test data";
        String wrongData = "Wrong data";

        // Use ComponentA to write data to log
        componentA.appendToLogFile(logFile.getAbsolutePath(), testData);

        // Verify with incorrect expected data
        boolean verificationResult = componentB.verifyLastLine(logFile.getAbsolutePath(), wrongData);

        // Assertion - should return false as the data doesn't match
        assertFalse(verificationResult, "Verification should fail when expected data doesn't match");
    }

    @Test
    void testComponentsIntegration_MultipleWrites() throws IOException {
        // Test data - simulate a process that writes multiple entries
        for (int i = 1; i <= 10; i++) {
            String data = "Log entry #" + i;
            componentA.appendToLogFile(logFile.getAbsolutePath(), data);

            // Verify after each write
            String lastLine = componentB.readLastLine(logFile.getAbsolutePath());
            assertEquals("Log entry #" + i, lastLine, "Last line should match the last entry");
        }

        // Final verification
        boolean finalVerification = componentB.verifyLastLine(logFile.getAbsolutePath(), "Log entry #10");
        assertTrue(finalVerification, "Final verification should match last entry");
    }

    @Test
    void testComponentsIntegration_NonExistentFile() {
        // Try to verify a non-existent file
        Exception exception = assertThrows(IOException.class, () -> {
            componentB.verifyLastLine(tempDir.resolve("non_existent_file.txt").toString(), "any data");
        });

        // Verify exception message contains file not found information
        assertTrue(exception.getMessage().contains("No such file") ||
                exception.getMessage().contains("cannot find") ||
                exception.getMessage().contains("not exist"),
                "Exception should indicate file not found");
    }
}