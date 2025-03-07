import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class ComponentIntegrationTest {
    private ComponentA componentA;
    private ComponentB componentB;
    private Path tempDir;
    private Path cloudDir;
    private Path downloadDir;
    private String testFileName;
    private String testContent;

    @BeforeEach
    void setUp() throws IOException {
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Create temporary directories for testing
        tempDir = Files.createTempDirectory("source");
        cloudDir = Files.createTempDirectory("cloud");
        downloadDir = Files.createTempDirectory("download");

        // Create a test file with content
        testFileName = "test.txt";
        testContent = "Test content for integration testing";
        Path sourceFile = tempDir.resolve(testFileName);
        Files.write(sourceFile, testContent.getBytes());
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up temporary directories
        deleteDirectory(tempDir);
        deleteDirectory(cloudDir);
        deleteDirectory(downloadDir);
    }

    @Test
    void testFileUploadAndDownload() throws IOException {
        // Test file upload
        String sourceFile = tempDir.resolve(testFileName).toString();
        componentA.uploadFile(sourceFile, cloudDir.toString());
        assertTrue(Files.exists(cloudDir.resolve(testFileName)), "File should exist in cloud directory");

        // Test file download
        componentB.downloadFile(cloudDir.toString(), testFileName, downloadDir.toString());
        assertTrue(Files.exists(downloadDir.resolve(testFileName)), "File should exist in download directory");

        // Verify downloaded content matches original
        boolean verified = componentB.verifyDownload(
                sourceFile,
                downloadDir.resolve(testFileName).toString());
        assertTrue(verified, "Downloaded file content should match original file content");
    }

    @Test
    void testErrorHandling() {
        // Test upload with non-existent source file
        assertThrows(IOException.class, () -> componentA.uploadFile("nonexistent.txt", cloudDir.toString()));

        // Test download with non-existent file
        assertThrows(IOException.class,
                () -> componentB.downloadFile(cloudDir.toString(), "nonexistent.txt", downloadDir.toString()));
    }

    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            // Handle deletion error
                        }
                    });
        }
    }
}