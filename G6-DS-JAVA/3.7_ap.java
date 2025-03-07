import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

public class ComponentIntegrationTest {

    @TempDir
    Path sourceDir;

    @TempDir
    Path backupDir;

    @TempDir
    Path restoreDir;

    private ComponentA componentA;
    private ComponentB componentB;

    // Test files content
    private final List<String> testFiles = Arrays.asList(
            "test1.txt", "test2.txt", "test3.txt");

    private final String fileContent = "This is test content";

    @BeforeEach
    void setUp() throws IOException {
        // Initialize components
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Create test files in source directory
        for (String fileName : testFiles) {
            Path filePath = sourceDir.resolve(fileName);
            Files.write(filePath, fileContent.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Test the complete backup and restore workflow
     * ComponentA backs up files from source to backup directory
     * ComponentB restores files from backup to restore directory
     * Verify the integrity of the restore
     */
    @Test
    void testBackupAndRestoreWorkflow() throws IOException {
        // Step 1: ComponentA backs up the source directory
        componentA.backupDirectory(sourceDir.toString(), backupDir.toString());

        // Verify backup was created
        for (String fileName : testFiles) {
            Path backupFile = backupDir.resolve(fileName);
            Assertions.assertTrue(Files.exists(backupFile), "Backup file should exist: " + fileName);
            String content = new String(Files.readAllBytes(backupFile), StandardCharsets.UTF_8);
            Assertions.assertEquals(fileContent, content, "File content should match");
        }

        // Step 2: ComponentB restores from backup to restore directory
        componentB.restoreBackup(backupDir.toString(), restoreDir.toString());

        // Verify restore was created
        for (String fileName : testFiles) {
            Path restoreFile = restoreDir.resolve(fileName);
            Assertions.assertTrue(Files.exists(restoreFile), "Restored file should exist: " + fileName);
            String content = new String(Files.readAllBytes(restoreFile), StandardCharsets.UTF_8);
            Assertions.assertEquals(fileContent, content, "Restored file content should match");
        }

        // Step 3: Verify the restore using ComponentB's verification method
        boolean verificationResult = componentB.verifyRestore(sourceDir.toString(), restoreDir.toString());
        Assertions.assertTrue(verificationResult, "Verification should confirm that restore matches source");
    }

    /**
     * Test the workflow with added files in source after backup
     * ComponentA backs up files from source to backup directory
     * Add new file to source directory
     * ComponentB restores from backup to restore directory
     * Verify restore doesn't contain the new file
     */
    @Test
    void testRestoreWithChangedSourceContent() throws IOException {
        // Step 1: ComponentA backs up the source directory
        componentA.backupDirectory(sourceDir.toString(), backupDir.toString());

        // Step 2: Add a new file to the source after backup
        String newFileName = "newFile.txt";
        Path newFilePath = sourceDir.resolve(newFileName);
        Files.write(newFilePath, "New content".getBytes(StandardCharsets.UTF_8));

        // Step 3: ComponentB restores from backup to restore directory
        componentB.restoreBackup(backupDir.toString(), restoreDir.toString());

        // Step 4: Verify restore doesn't contain the new file
        Path newFileInRestore = restoreDir.resolve(newFileName);
        Assertions.assertFalse(Files.exists(newFileInRestore), "Restored directory should not contain the new file");

        // Step 5: Verify the restore using ComponentB's verification method
        boolean verificationResult = componentB.verifyRestore(sourceDir.toString(), restoreDir.toString());
        Assertions.assertFalse(verificationResult, "Verification should fail as source and restore don't match");
    }

    /**
     * Test the workflow with empty source directory
     * ComponentA backs up empty source to backup directory
     * ComponentB restores from backup to restore directory
     * Verify the restore is also empty
     */
    @Test
    void testBackupAndRestoreEmptyDirectory() throws IOException {
        // Create an empty source directory
        Path emptySourceDir = Files.createTempDirectory("emptySource");
        Path emptyBackupDir = Files.createTempDirectory("emptyBackup");
        Path emptyRestoreDir = Files.createTempDirectory("emptyRestore");

        try {
            // Step 1: ComponentA backs up the empty source directory
            componentA.backupDirectory(emptySourceDir.toString(), emptyBackupDir.toString());

            // Step 2: ComponentB restores from backup to restore directory
            componentB.restoreBackup(emptyBackupDir.toString(), emptyRestoreDir.toString());

            // Step 3: Verify the restore using ComponentB's verification method
            boolean verificationResult = componentB.verifyRestore(emptySourceDir.toString(),
                    emptyRestoreDir.toString());
            Assertions.assertTrue(verificationResult, "Verification should confirm that empty directories match");

            // Verify no files in restore directory
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(emptyRestoreDir)) {
                Assertions.assertFalse(stream.iterator().hasNext(), "Restore directory should be empty");
            }
        } finally {
            // Clean up
            Files.deleteIfExists(emptySourceDir);
            Files.deleteIfExists(emptyBackupDir);
            Files.deleteIfExists(emptyRestoreDir);
        }
    }

    /**
     * Test error handling when the backup directory doesn't exist
     * ComponentB tries to restore from non-existent backup
     */
    @Test
    void testRestoreFromNonExistentBackup() {
        // Path to a non-existent directory
        Path nonExistentDir = Paths.get("non_existent_backup");

        // Try to restore from non-existent backup
        IOException exception = Assertions.assertThrows(IOException.class, () -> {
            componentB.restoreBackup(nonExistentDir.toString(), restoreDir.toString());
        });

        // Verify exception message contains expected information
        Assertions.assertTrue(exception.getMessage().contains("non_existent_backup") ||
                exception.getMessage().contains("NoSuchFileException"),
                "Exception should indicate the directory doesn't exist");
    }

    /**
     * Test partial backup and restore
     * Create backup of some files
     * Add more files to backup directory manually
     * Restore all files including manually added ones
     */
    @Test
    void testPartialBackupAndFullRestore() throws IOException {
        // Step 1: ComponentA backs up the source directory (partial, using first 2
        // files only)
        Path partialSourceDir = Files.createTempDirectory("partialSource");

        try {
            // Create only the first 2 test files
            for (int i = 0; i < 2; i++) {
                String fileName = testFiles.get(i);
                Path filePath = partialSourceDir.resolve(fileName);
                Files.write(filePath, fileContent.getBytes(StandardCharsets.UTF_8));
            }

            // Perform partial backup
            componentA.backupDirectory(partialSourceDir.toString(), backupDir.toString());

            // Step 2: Manually add another file to backup directory
            String extraFileName = "extra.txt";
            Path extraFilePath = backupDir.resolve(extraFileName);
            Files.write(extraFilePath, "Extra content".getBytes(StandardCharsets.UTF_8));

            // Step 3: ComponentB restores from backup to restore directory
            componentB.restoreBackup(backupDir.toString(), restoreDir.toString());

            // Step 4: Verify all files were restored including the manually added one
            for (int i = 0; i < 2; i++) {
                Path restoreFile = restoreDir.resolve(testFiles.get(i));
                Assertions.assertTrue(Files.exists(restoreFile), "Restored file should exist: " + testFiles.get(i));
            }

            Path extraRestoreFile = restoreDir.resolve(extraFileName);
            Assertions.assertTrue(Files.exists(extraRestoreFile), "Extra file should be restored");
            String content = new String(Files.readAllBytes(extraRestoreFile), StandardCharsets.UTF_8);
            Assertions.assertEquals("Extra content", content, "Extra file content should match");
        } finally {
            // Clean up
            Files.deleteIfExists(partialSourceDir);
        }
    }
}