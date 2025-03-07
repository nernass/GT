import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ComponentIntegrationTest {

    @TempDir
    Path sourceDir;

    @TempDir
    Path backupDir;

    @TempDir
    Path restoreDir;

    private ComponentA componentA;
    private ComponentB componentB;

    private List<String> testFiles = Arrays.asList(
            "file1.txt",
            "file2.txt",
            "file3.txt");

    private List<String> testContents = Arrays.asList(
            "Content of file 1",
            "Content of file 2",
            "Content of file 3");

    @BeforeEach
    void setUp() throws IOException {
        // Initialize components
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Create test files in source directory
        for (int i = 0; i < testFiles.size(); i++) {
            Path filePath = sourceDir.resolve(testFiles.get(i));
            Files.writeString(filePath, testContents.get(i));
        }
    }

    @AfterEach
    void tearDown() {
        // Nothing to clean up as TempDir is handled automatically
    }

    @Test
    void testFullBackupAndRestoreFlow() throws IOException {
        // Test full integration workflow
        // 1. Backup from source to backup directory
        componentA.backupDirectory(sourceDir.toString(), backupDir.toString());

        // Verify backup was created
        for (String fileName : testFiles) {
            Path backupFile = backupDir.resolve(fileName);
            assertTrue(Files.exists(backupFile), "Backup file should exist: " + fileName);
        }

        // 2. Restore from backup to restore directory
        componentB.restoreBackup(backupDir.toString(), restoreDir.toString());

        // Verify restore files exist
        for (String fileName : testFiles) {
            Path restoreFile = restoreDir.resolve(fileName);
            assertTrue(Files.exists(restoreFile), "Restored file should exist: " + fileName);
        }

        // 3. Verify contents of restored files match source
        for (int i = 0; i < testFiles.size(); i++) {
            Path sourceFile = sourceDir.resolve(testFiles.get(i));
            Path restoreFile = restoreDir.resolve(testFiles.get(i));

            String sourceContent = Files.readString(sourceFile);
            String restoredContent = Files.readString(restoreFile);

            assertEquals(sourceContent, restoredContent,
                    "Content of restored file should match source for: " + testFiles.get(i));
        }

        // 4. Use ComponentB's verification method
        boolean verificationResult = componentB.verifyRestore(
                sourceDir.toString(), restoreDir.toString());
        assertTrue(verificationResult, "Verification should confirm files match");
    }

    @Test
    void testPartialBackupAndRestore() throws IOException {
        // Create initial backup
        componentA.backupDirectory(sourceDir.toString(), backupDir.toString());

        // Add a new file to source after backup
        String newFileName = "newFile.txt";
        String newContent = "This file was added after backup";
        Files.writeString(sourceDir.resolve(newFileName), newContent);

        // Restore from backup to restore directory
        componentB.restoreBackup(backupDir.toString(), restoreDir.toString());

        // Verify new file is not in restore directory
        Path restoredNewFile = restoreDir.resolve(newFileName);
        assertFalse(Files.exists(restoredNewFile),
                "New file added after backup should not be in restore directory");

        // Verify using ComponentB's method (should return false as files don't match)
        boolean verificationResult = componentB.verifyRestore(
                sourceDir.toString(), restoreDir.toString());
        assertFalse(verificationResult,
                "Verification should fail because source has an extra file");
    }

    @Test
    void testModifiedFilesBackupAndRestore() throws IOException {
        // Create initial backup
        componentA.backupDirectory(sourceDir.toString(), backupDir.toString());

        // Modify a file in the source after backup
        String modifiedContent = "This content was modified after backup";
        Files.writeString(sourceDir.resolve(testFiles.get(0)), modifiedContent,
                StandardOpenOption.TRUNCATE_EXISTING);

        // Restore from backup to restore directory
        componentB.restoreBackup(backupDir.toString(), restoreDir.toString());

        // File should be restored to its original content, not the modified version
        String restoredContent = Files.readString(restoreDir.resolve(testFiles.get(0)));
        assertEquals(testContents.get(0), restoredContent,
                "Restored file should have original content, not modified content");

        // Verify the difference between source and restore
        boolean verificationResult = componentB.verifyRestore(
                sourceDir.toString(), restoreDir.toString());
        assertFalse(verificationResult,
                "Verification should fail because a source file was modified");
    }

    @Test
    void testEmptyDirectoryBackupAndRestore() throws IOException {
        // Create a new empty source directory
        Path emptySourceDir = Files.createTempDirectory("emptySource");
        Path emptyBackupDir = Files.createTempDirectory("emptyBackup");
        Path emptyRestoreDir = Files.createTempDirectory("emptyRestore");

        try {
            // Backup empty directory
            componentA.backupDirectory(emptySourceDir.toString(), emptyBackupDir.toString());

            // Restore from empty backup
            componentB.restoreBackup(emptyBackupDir.toString(), emptyRestoreDir.toString());

            // Verify empty directories are handled correctly
            boolean verificationResult = componentB.verifyRestore(
                    emptySourceDir.toString(), emptyRestoreDir.toString());
            assertTrue(verificationResult,
                    "Verification should pass for empty directories");
        } finally {
            // Clean up temporary directories
            Files.deleteIfExists(emptySourceDir);
            Files.deleteIfExists(emptyBackupDir);
            Files.deleteIfExists(emptyRestoreDir);
        }
    }
}