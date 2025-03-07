import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    private Path testFilePath;
    private Path zipFilePath;
    private Path extractionDirPath;

    @BeforeEach
    public void setUp(@TempDir Path tempDir) throws IOException {
        // Initialize components
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Create test file paths
        testFilePath = tempDir.resolve("testFile.txt");
        zipFilePath = tempDir.resolve("compressed.zip");
        extractionDirPath = tempDir.resolve("extracted");

        // Create a test file with content
        Files.writeString(testFilePath, "This is test content for integration testing");

        // Create extraction directory
        Files.createDirectory(extractionDirPath);
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Clean up files if they exist
        Files.deleteIfExists(testFilePath);
        Files.deleteIfExists(zipFilePath);
        if (Files.exists(extractionDirPath)) {
            Files.walk(extractionDirPath)
                    .sorted((a, b) -> b.compareTo(a)) // Reverse order to delete contents first
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            System.err.println("Failed to delete: " + path);
                        }
                    });
        }
    }

    @Test
    public void testCompressAndExtractIntegration() throws IOException {
        // Scenario 1: Success path - End-to-end workflow
        // Step 1: Use ComponentA to compress the file
        componentA.compressFile(testFilePath.toString(), zipFilePath.toString());

        // Verify the ZIP file was created
        assertTrue(Files.exists(zipFilePath), "ZIP file should be created");

        // Step 2: Use ComponentB to extract the ZIP file
        componentB.extractZipFile(zipFilePath.toString(), extractionDirPath.toString());

        // Step 3: Verify the extracted file exists
        Path extractedFilePath = extractionDirPath.resolve(testFilePath.getFileName());
        assertTrue(Files.exists(extractedFilePath), "Extracted file should exist");

        // Step 4: Verify the content of the extracted file matches the original
        boolean filesMatch = componentB.verifyExtractedFile(
                testFilePath.toString(),
                extractedFilePath.toString());

        assertTrue(filesMatch, "The extracted file content should match the original file");
    }

    @Test
    public void testCompressAndExtractWithLargeFile() throws IOException {
        // Scenario 3: Edge case - Large file handling
        // Create a larger test file (1MB)
        StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 1024 * 1024 / 10; i++) {
            largeContent.append("0123456789");
        }
        Files.writeString(testFilePath, largeContent.toString());

        // Step 1: Use ComponentA to compress the file
        componentA.compressFile(testFilePath.toString(), zipFilePath.toString());

        // Step 2: Use ComponentB to extract the ZIP file
        componentB.extractZipFile(zipFilePath.toString(), extractionDirPath.toString());

        // Step 3: Verify the extracted file exists and matches
        Path extractedFilePath = extractionDirPath.resolve(testFilePath.getFileName());
        boolean filesMatch = componentB.verifyExtractedFile(
                testFilePath.toString(),
                extractedFilePath.toString());

        assertTrue(filesMatch, "The extracted large file content should match the original");
    }

    @Test
    public void testCompressAndExtractWithEmptyFile() throws IOException {
        // Scenario 3: Edge case - Empty file handling
        // Create an empty test file
        Files.writeString(testFilePath, "");

        // Step 1: Use ComponentA to compress the file
        componentA.compressFile(testFilePath.toString(), zipFilePath.toString());

        // Step 2: Use ComponentB to extract the ZIP file
        componentB.extractZipFile(zipFilePath.toString(), extractionDirPath.toString());

        // Step 3: Verify the extracted file exists and matches
        Path extractedFilePath = extractionDirPath.resolve(testFilePath.getFileName());
        boolean filesMatch = componentB.verifyExtractedFile(
                testFilePath.toString(),
                extractedFilePath.toString());

        assertTrue(filesMatch, "The extracted empty file should match the original empty file");
    }

    @Test
    public void testExceptionHandlingWithInvalidZipFile() {
        // Scenario 2: Failure path - Invalid ZIP handling
        // Create a non-zip file
        try {
            Files.writeString(zipFilePath, "This is not a valid ZIP file");

            // Try to extract the invalid ZIP file
            assertThrows(IOException.class, () -> {
                componentB.extractZipFile(zipFilePath.toString(), extractionDirPath.toString());
            }, "Should throw IOException when extracting invalid ZIP file");

        } catch (IOException e) {
            fail("Test setup failed: " + e.getMessage());
        }
    }
}