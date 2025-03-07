import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {
    private ComponentA componentA;
    private ComponentB componentB;
    private String sourceDir;
    private String backupDir;
    private String restoreDir;

    @BeforeEach
    void setUp() throws IOException {
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Create test directories
        sourceDir = "test_source";
        backupDir = "test_backup";
        restoreDir = "test_restore";

        // Create source directory and sample files
        Files.createDirectories(Paths.get(sourceDir));
        Files.writeString(Paths.get(sourceDir, "file1.txt"), "Test content 1");
        Files.writeString(Paths.get(sourceDir, "file2.txt"), "Test content 2");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up test directories
        deleteDirectory(Paths.get(sourceDir));
        deleteDirectory(Paths.get(backupDir));
        deleteDirectory(Paths.get(restoreDir));
    }

    @Test
    void testBackupAndRestore() throws IOException {
        // Test backup process
        componentA.backupDirectory(sourceDir, backupDir);
        assertTrue(Files.exists(Paths.get(backupDir)));
        assertTrue(Files.exists(Paths.get(backupDir, "file1.txt")));
        assertTrue(Files.exists(Paths.get(backupDir, "file2.txt")));

        // Test restore process
        componentB.restoreBackup(backupDir, restoreDir);
        assertTrue(Files.exists(Paths.get(restoreDir)));
        assertTrue(Files.exists(Paths.get(restoreDir, "file1.txt")));
        assertTrue(Files.exists(Paths.get(restoreDir, "file2.txt")));

        // Verify restoration
        assertTrue(componentB.verifyRestore(sourceDir, restoreDir));
    }

    @Test
    void testBackupWithEmptyDirectory() throws IOException {
        // Delete sample files from source directory
        deleteDirectory(Paths.get(sourceDir));
        Files.createDirectories(Paths.get(sourceDir));

        // Test backup and restore with empty directory
        componentA.backupDirectory(sourceDir, backupDir);
        componentB.restoreBackup(backupDir, restoreDir);
        assertTrue(componentB.verifyRestore(sourceDir, restoreDir));
    }

    @Test
    void testNonExistentSourceDirectory() {
        String nonExistentDir = "nonexistent_dir";
        assertThrows(IOException.class, () -> componentA.backupDirectory(nonExistentDir, backupDir));
    }

    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted(java.util.Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(java.io.File::delete);
        }
    }
}