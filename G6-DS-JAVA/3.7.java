import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {

    @TempDir
    Path sourceDir;

    @TempDir
    Path backupDir;

    @TempDir
    Path restoreDir;

    private ComponentA componentA;
    private ComponentB componentB;
    private List<String> testFileContents;

    @BeforeEach
    void setUp() throws IOException {
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Create test files in the source directory
        testFileContents = Arrays.asList("File 1 content", "File 2 content", "File 3 content");

        for (int i = 0; i < testFileContents.size(); i++) {
            Path testFile = sourceDir.resolve("testFile" + (i + 1) + ".txt");
            Files.writeString(testFile, testFileContents.get(i));
        }
    }

    @Test
    void testBackupAndRestore() throws IOException {
        // Perform backup using ComponentA
        componentA.backupDirectory(sourceDir.toString(), backupDir.toString());

        // Verify backup directory contains the same files
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(backupDir)) {
            int fileCount = 0;
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    fileCount++;
                    String content = Files.readString(file);
                    assertTrue(testFileContents.contains(content),
                            "Backup file content should match one of the source files");
                }
            }
            assertEquals(testFileContents.size(), fileCount, "Backup should contain all source files");
        }

        // Perform restore using ComponentB
        componentB.restoreBackup(backupDir.toString(), restoreDir.toString());

        // Verify restore was successful using ComponentB's verification method
        boolean verificationResult = componentB.verifyRestore(sourceDir.toString(), restoreDir.toString());
        assertTrue(verificationResult, "Verification should confirm restore matches source");

        // Additional verification: check file content is preserved
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(restoreDir)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    String content = Files.readString(file);
                    assertTrue(testFileContents.contains(content),
                            "Restored file content should match one of the source files");
                }
            }
        }
    }

    @Test
    void testBackupAndRestoreWithEmptyDirectory() throws IOException {
        // Clean up source directory
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir)) {
            for (Path file : stream) {
                Files.delete(file);
            }
        }

        // Perform backup with empty source
        componentA.backupDirectory(sourceDir.toString(), backupDir.toString());

        // Restore from empty backup
        componentB.restoreBackup(backupDir.toString(), restoreDir.toString());

        // Verify restore was successful
        boolean verificationResult = componentB.verifyRestore(sourceDir.toString(), restoreDir.toString());
        assertTrue(verificationResult, "Empty directory verification should succeed");

        // Check that restore directory is empty
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(restoreDir)) {
            int fileCount = 0;
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    fileCount++;
                }
            }
            assertEquals(0, fileCount, "Restore directory should be empty");
        }
    }

    @Test
    void testNonExistentSourceDirectory() {
        Path nonExistentDir = Paths.get("non_existent_directory");

        IOException exception = assertThrows(IOException.class, () -> {
            componentA.backupDirectory(nonExistentDir.toString(), backupDir.toString());
        });

        assertTrue(exception.getMessage().contains("non_existent_directory") ||
                exception instanceof NoSuchFileException,
                "Should throw IOException for non-existent source directory");
    }

    @Test
    void testVerifyRestoreWithDifferentContent() throws IOException {
        // Perform backup using ComponentA
        componentA.backupDirectory(sourceDir.toString(), backupDir.toString());

        // Create different content in restore directory
        Files.createDirectories(restoreDir);
        Files.writeString(restoreDir.resolve("differentFile.txt"), "Different content");

        // Verify should return false
        boolean verificationResult = componentB.verifyRestore(sourceDir.toString(), restoreDir.toString());
        assertFalse(verificationResult, "Verification should fail with different content");
    }
}