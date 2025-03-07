
// IntegrationTest.java
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    private static final String SOURCE_DIR = "test_source";
    private static final String BACKUP_DIR = "test_backup";
    private static final String RESTORE_DIR = "test_restore";

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() throws IOException {
        // Initialize components
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Create the source directory and add some test files
        Files.createDirectories(Paths.get(SOURCE_DIR));
        Files.write(Paths.get(SOURCE_DIR, "file1.txt"), "Hello, World!".getBytes());
        Files.write(Paths.get(SOURCE_DIR, "file2.txt"), "Backup and Restore Test".getBytes());
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Clean up directories after each test
        deleteDirectory(Paths.get(SOURCE_DIR));
        deleteDirectory(Paths.get(BACKUP_DIR));
        deleteDirectory(Paths.get(RESTORE_DIR));
    }

    @Test
    public void testIntegration() throws IOException {
        // Step 1: Back up the source directory using Component A
        componentA.backupDirectory(SOURCE_DIR, BACKUP_DIR);

        // Step 2: Restore the backup using Component B
        componentB.restoreBackup(BACKUP_DIR, RESTORE_DIR);

        // Step 3: Verify that the restored files match the source files
        boolean isVerified = componentB.verifyRestore(SOURCE_DIR, RESTORE_DIR);
        assertTrue(isVerified, "Restored files do not match the source files");
    }

    /**
     * Deletes a directory and all its contents.
     *
     * @param path The path to the directory to delete.
     * @throws IOException If an I/O error occurs during deletion.
     */
    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            System.err.println("Failed to delete file: " + file);
                        }
                    });
        }
    }
}