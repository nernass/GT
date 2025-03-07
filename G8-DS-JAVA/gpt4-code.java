import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    private Path tempDir;
    private Path cloudDir;
    private Path sourceFile;
    private Path downloadedFile;

    @BeforeEach
    public void setUp() throws IOException {
        componentA = new ComponentA();
        componentB = new ComponentB();
        tempDir = Files.createTempDirectory("testDir");
        cloudDir = tempDir.resolve("cloud");
        sourceFile = tempDir.resolve("sourceFile.txt");
        downloadedFile = tempDir.resolve("downloadedFile.txt");

        // Create a sample file to upload
        Files.write(sourceFile, "Sample content".getBytes());
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.walk(tempDir)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
    }

    @Test
    public void testUploadAndDownload() throws IOException {
        // Upload the file
        componentA.uploadFile(sourceFile.toString(), cloudDir.toString());

        // Download the file
        componentB.downloadFile(cloudDir.toString(), sourceFile.getFileName().toString(), tempDir.toString());

        // Verify the downloaded file
        boolean isVerified = componentB.verifyDownload(sourceFile.toString(), tempDir.resolve(sourceFile.getFileName()).toString());
        assertTrue(isVerified, "The downloaded file content should match the original file content");
    }

    @Test
    public void testDownloadNonExistentFile() {
        assertThrows(IOException.class, () -> {
            componentB.downloadFile(cloudDir.toString(), "nonExistentFile.txt", tempDir.toString());
        });
    }
}