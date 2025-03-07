import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ComponentIntegrationTest {

    @Mock
    private ComponentA componentA;

    @Mock
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    public void testSuccessPath() throws IOException {
        String sourceDir = "sourceDir";
        String backupDir = "backupDir";
        String restoreDir = "restoreDir";

        // Mock the file system operations
        Files.createDirectories(Paths.get(sourceDir));
        Files.createDirectories(Paths.get(backupDir));
        Files.createDirectories(Paths.get(restoreDir));

        // Execute the workflow
        componentA.backupDirectory(sourceDir, backupDir);
        componentB.restoreBackup(backupDir, restoreDir);

        // Verify the restore
        boolean result = componentB.verifyRestore(sourceDir, restoreDir);
        assertTrue(result, "The files should match after restore");
    }

    @Test
    public void testPartialFailure() throws IOException {
        String sourceDir = "sourceDir";
        String backupDir = "backupDir";
        String restoreDir = "restoreDir";

        // Mock the file system operations
        Files.createDirectories(Paths.get(sourceDir));
        Files.createDirectories(Paths.get(backupDir));
        Files.createDirectories(Paths.get(restoreDir));

        // Mock ComponentB to throw an exception during restore
        doThrow(new IOException("Restore failed")).when(componentB).restoreBackup(backupDir, restoreDir);

        // Execute the workflow and expect an exception
        componentA.backupDirectory(sourceDir, backupDir);
        assertThrows(IOException.class, () -> componentB.restoreBackup(backupDir, restoreDir), "Expected IOException");

        // Verify no files were restored
        boolean result = componentB.verifyRestore(sourceDir, restoreDir);
        assertFalse(result, "The files should not match after failed restore");
    }

    @Test
    public void testEdgeCase() throws IOException {
        String sourceDir = "sourceDir";
        String backupDir = "backupDir";
        String restoreDir = "restoreDir";

        // Mock the file system operations
        Files.createDirectories(Paths.get(sourceDir));
        Files.createDirectories(Paths.get(backupDir));
        Files.createDirectories(Paths.get(restoreDir));

        // Create an empty source directory
        // Execute the workflow
        componentA.backupDirectory(sourceDir, backupDir);
        componentB.restoreBackup(backupDir, restoreDir);

        // Verify the restore
        boolean result = componentB.verifyRestore(sourceDir, restoreDir);
        assertTrue(result, "The files should match after restore with empty source directory");
    }
}