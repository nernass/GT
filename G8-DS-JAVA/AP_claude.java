import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

/**
 * Integration test to verify the interaction between ComponentA and ComponentB
 * in the file upload-download process.
 */
public class ComponentIntegrationTest {
    private ComponentA componentA;
    private ComponentB componentB;
    private Path tempDir;
    private Path sourceDir;
    private Path cloudDir;
    private Path downloadDir;
    private static final String TEST_FILENAME = "testFile.txt";
    private static final String TEST_CONTENT = "This is test content for integration testing.";

    @BeforeEach
    public void setUp() throws IOException {
        // Initialize components
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Create temporary directories for testing
        tempDir = Files.createTempDirectory("integration_test");
        sourceDir = Files.createDirectory(tempDir.resolve("source"));
        cloudDir = Files.createDirectory(tempDir.resolve("cloud"));
        downloadDir = Files.createDirectory(tempDir.resolve("download"));

        // Create a test file in the source directory
        Path sourceFile = sourceDir.resolve(TEST_FILENAME);
        Files.write(sourceFile, TEST_CONTENT.getBytes());
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Clean up temporary directories
        Files.walk(tempDir)
                .sorted(java.util.Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    public void testCompleteUploadDownloadWorkflow() throws IOException {
        // Test file paths
        String sourceFilePath = sourceDir.resolve(TEST_FILENAME).toString();
        String cloudDirPath = cloudDir.toString();
        String downloadDirPath = downloadDir.toString();

        // Step 1: Upload file using ComponentA
        componentA.uploadFile(sourceFilePath, cloudDirPath);

        // Verify file was successfully uploaded to cloud directory
        Path uploadedFile = cloudDir.resolve(TEST_FILENAME);
        Assertions.assertTrue(Files.exists(uploadedFile),
                "File should be uploaded to cloud directory");

        // Step 2: Download file using ComponentB
        componentB.downloadFile(cloudDirPath, TEST_FILENAME, downloadDirPath);

        // Verify file was successfully downloaded
        Path downloadedFile = downloadDir.resolve(TEST_FILENAME);
        Assertions.assertTrue(Files.exists(downloadedFile),
                "File should be downloaded to download directory");

        // Step 3: Verify content integrity using ComponentB's verification method
        boolean contentMatches = componentB.verifyDownload(
                sourceFilePath, downloadedFile.toString());
        Assertions.assertTrue(contentMatches,
                "Downloaded file content should match source file content");
    }

    @Test
    public void testErrorHandlingWithInvalidPaths() {
        // Test with non-existent source file
        String nonExistentFile = sourceDir.resolve("nonexistent.txt").toString();
        String cloudDirPath = cloudDir.toString();

        // ComponentA should throw an exception when source file doesn't exist
        Assertions.assertThrows(IOException.class, () -> {
            componentA.uploadFile(nonExistentFile, cloudDirPath);
        }, "Should throw IOException when source file doesn't exist");

        // Test download from non-existent cloud directory
        String downloadDirPath = downloadDir.toString();
        String invalidCloudDir = tempDir.resolve("non_existent").toString();

        // ComponentB should throw an exception when cloud directory doesn't exist
        Assertions.assertThrows(IOException.class, () -> {
            componentB.downloadFile(invalidCloudDir, TEST_FILENAME, downloadDirPath);
        }, "Should throw IOException when cloud directory doesn't exist");
    }

    @Test
    public void testEmptyFileHandling() throws IOException {
        // Create empty test file
        String emptyFileName = "emptyFile.txt";
        Path emptyFilePath = sourceDir.resolve(emptyFileName);
        Files.createFile(emptyFilePath);

        String sourceFilePath = emptyFilePath.toString();
        String cloudDirPath = cloudDir.toString();
        String downloadDirPath = downloadDir.toString();

        // Complete workflow with empty file
        componentA.uploadFile(sourceFilePath, cloudDirPath);
        componentB.downloadFile(cloudDirPath, emptyFileName, downloadDirPath);

        // Verify empty file integrity
        Path downloadedEmptyFile = downloadDir.resolve(emptyFileName);
        boolean contentMatches = componentB.verifyDownload(
                sourceFilePath, downloadedEmptyFile.toString());

        Assertions.assertTrue(contentMatches,
                "Empty file content should match after upload and download");
        Assertions.assertEquals(0, Files.size(downloadedEmptyFile),
                "Downloaded file should be empty");
    }

    @Test
    public void testLargeFileTransfer() throws IOException {
        // Create a larger test file (1MB)
        String largeFileName = "largeFile.dat";
        Path largeFilePath = sourceDir.resolve(largeFileName);
        byte[] largeContent = new byte[1024 * 1024]; // 1MB of data
        new java.util.Random().nextBytes(largeContent);
        Files.write(largeFilePath, largeContent);

        String sourceFilePath = largeFilePath.toString();
        String cloudDirPath = cloudDir.toString();
        String downloadDirPath = downloadDir.toString();

        // Execute the complete workflow
        componentA.uploadFile(sourceFilePath, cloudDirPath);
        componentB.downloadFile(cloudDirPath, largeFileName, downloadDirPath);

        // Verify large file integrity
        Path downloadedLargeFile = downloadDir.resolve(largeFileName);
        boolean contentMatches = componentB.verifyDownload(
                sourceFilePath, downloadedLargeFile.toString());

        Assertions.assertTrue(contentMatches,
                "Large file content should match after upload and download");
        Assertions.assertEquals(Files.size(largeFilePath), Files.size(downloadedLargeFile),
                "Downloaded file size should match original");
    }
}