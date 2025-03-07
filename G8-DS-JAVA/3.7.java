import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    private String testContent = "This is test content for integration testing";

    @TempDir
    Path sourceDir;

    @TempDir
    Path cloudDir;

    @TempDir
    Path downloadDir;

    private Path sourceFile;
    private String fileName = "testFile.txt";

    @BeforeEach
    void setUp() throws IOException {
        // Initialize the components
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Create a test file in the source directory
        sourceFile = sourceDir.resolve(fileName);
        Files.writeString(sourceFile, testContent);
    }

    @Test
    void testFileUploadAndDownloadIntegration() throws IOException {
        // Test the upload functionality
        componentA.uploadFile(sourceFile.toString(), cloudDir.toString());

        // Verify the file exists in cloud directory
        Path cloudFile = cloudDir.resolve(fileName);
        Assertions.assertTrue(Files.exists(cloudFile), "File should exist in cloud directory after upload");

        // Test the download functionality
        componentB.downloadFile(cloudDir.toString(), fileName, downloadDir.toString());

        // Verify the file exists in download directory
        Path downloadedFile = downloadDir.resolve(fileName);
        Assertions.assertTrue(Files.exists(downloadedFile), "File should exist in download directory after download");

        // Verify the file content matches
        boolean contentMatches = componentB.verifyDownload(sourceFile.toString(), downloadedFile.toString());
        Assertions.assertTrue(contentMatches, "Downloaded file content should match the source file");
    }

    @Test
    void testFileUploadAndDownloadWithNonExistentFile() {
        // Test with non-existent source file
        String nonExistentFile = sourceDir.resolve("nonExistent.txt").toString();

        // Upload should throw exception for non-existent file
        IOException uploadException = Assertions.assertThrows(
                IOException.class,
                () -> componentA.uploadFile(nonExistentFile, cloudDir.toString()));
        Assertions.assertTrue(uploadException.getMessage().contains("nonExistent.txt"));

        // Download should throw exception for non-existent file
        IOException downloadException = Assertions.assertThrows(
                IOException.class,
                () -> componentB.downloadFile(cloudDir.toString(), "nonExistent.txt", downloadDir.toString()));
        Assertions.assertTrue(downloadException.getMessage().contains("nonExistent.txt"));
    }

    @Test
    void testVerifyDownloadWithCorruptedFile() throws IOException {
        // Upload the original file
        componentA.uploadFile(sourceFile.toString(), cloudDir.toString());

        // Download the file
        componentB.downloadFile(cloudDir.toString(), fileName, downloadDir.toString());

        // Corrupt the downloaded file by writing different content
        Path downloadedFile = downloadDir.resolve(fileName);
        Files.writeString(downloadedFile, "Corrupted content");

        // Verify should return false for corrupted file
        boolean contentMatches = componentB.verifyDownload(sourceFile.toString(), downloadedFile.toString());
        Assertions.assertFalse(contentMatches, "Verification should fail for corrupted file");
    }
}