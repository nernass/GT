import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class ComponentIntegrationTest {

    private ComponentA compressor;
    private ComponentB extractor;
    @TempDir
    Path tempDir;
    private File testFile;
    private File zipFile;
    private File extractDir;

    @BeforeEach
    void setUp() throws IOException {
        compressor = new ComponentA();
        extractor = new ComponentB();

        // Create test file with sample content
        testFile = tempDir.resolve("test.txt").toFile();
        Files.writeString(testFile.toPath(), "Test content for integration");

        // Set up zip file path and extraction directory
        zipFile = tempDir.resolve("test.zip").toFile();
        extractDir = tempDir.resolve("extracted").toFile();
        extractDir.mkdir();
    }

    @Test
    void testCompressAndExtractWorkflow() throws IOException {
        // Compress the test file
        compressor.compressFile(testFile.getPath(), zipFile.getPath());
        assertTrue(zipFile.exists(), "ZIP file should be created");
        assertTrue(zipFile.length() > 0, "ZIP file should not be empty");

        // Extract the compressed file
        extractor.extractZipFile(zipFile.getPath(), extractDir.getPath());
        File extractedFile = new File(extractDir, testFile.getName());
        assertTrue(extractedFile.exists(), "Extracted file should exist");

        // Verify the extracted content matches original
        boolean isMatch = extractor.verifyExtractedFile(testFile.getPath(), extractedFile.getPath());
        assertTrue(isMatch, "Extracted file content should match original");
    }

    @Test
    void testCompressNonExistentFile() {
        assertThrows(IOException.class, () -> {
            compressor.compressFile("nonexistent.txt", zipFile.getPath());
        }, "Should throw IOException for non-existent source file");
    }

    @Test
    void testExtractInvalidZip() {
        assertThrows(IOException.class, () -> {
            extractor.extractZipFile("invalid.zip", extractDir.getPath());
        }, "Should throw IOException for invalid ZIP file");
    }

    @Test
    void testCompressAndExtractEmptyFile() throws IOException {
        // Create empty test file
        File emptyFile = tempDir.resolve("empty.txt").toFile();
        emptyFile.createNewFile();

        // Test compression and extraction of empty file
        File emptyZip = tempDir.resolve("empty.zip").toFile();
        compressor.compressFile(emptyFile.getPath(), emptyZip.getPath());
        extractor.extractZipFile(emptyZip.getPath(), extractDir.getPath());

        File extractedEmptyFile = new File(extractDir, emptyFile.getName());
        assertTrue(extractedEmptyFile.exists(), "Extracted empty file should exist");
        assertEquals(0, extractedEmptyFile.length(), "Extracted file should be empty");
    }
}