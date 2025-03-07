import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

public class ComponentIntegrationTest {

    private static final String SOURCE_DIR = "test_source_dir";
    private static final String BACKUP_DIR = "test_backup_dir";
    private static final String RESTORE_DIR = "test_restore_dir";

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() throws IOException {
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Create test directories and files
        Files.createDirectories(Paths.get(SOURCE_DIR));
        Files.createDirectories(Paths.get(BACKUP_DIR));
        Files.createDirectories(Paths.get(RESTORE_DIR));

        // Create a sample file in the source directory
        Files.write(Paths.get(SOURCE_DIR, "testfile.txt"), "Sample content".getBytes());
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Clean up test directories and files
        deleteDirectory(Paths.get(SOURCE_DIR));
        deleteDirectory(Paths.get(BACKUP_DIR));
        deleteDirectory(Paths.get(RESTORE_DIR));
    }

    @Test
    public void testBackupAndRestore() throws IOException {
        // Perform backup
        componentA.backupDirectory(SOURCE_DIR, BACKUP_DIR);

        // Perform restore
        componentB.restoreBackup(BACKUP_DIR, RESTORE_DIR);

        // Verify restore
        assertTrue(componentB.verifyRestore(SOURCE_DIR, RESTORE_DIR));
    }

    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path entry : stream) {
                    if (Files.isDirectory(entry)) {
                        deleteDirectory(entry);
                    } else {
                        Files.delete(entry);
                    }
                }
            }
            Files.delete(path);
        }
    }
}