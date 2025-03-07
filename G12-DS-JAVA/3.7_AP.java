import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @TempDir
    Path tempDir;

    private Path sourceFilePath;
    private Path zipFilePath;
    private Path outputDirPath;

    @BeforeEach
    void setUp() throws IOException {
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Create a test file with content
        sourceFilePath = tempDir.resolve("testFile.txt");
        Files.writeString(sourceFilePath, "This is a test file content for integration testing.");

        // Define paths for zip and extraction
        zipFilePath = tempDir.resolve("compressed.zip");
        outputDirPath = tempDir.resolve("extracted");
        Files.createDirectories(outputDirPath);
    }

    @AfterEach
    void cleanUp() {
        // Clean up resources if needed
    }

    @Test
    void testCompressAndExtractWorkflow() throws IOException {
        // Step 1: ComponentA compresses the file
        componentA.compressFile(sourceFilePath.toString(), zipFilePath.toString());

        // Verify zip file was created
        assertTrue(Files.exists(zipFilePath), "Zip file should exist after compression");
        assertTrue(Files.size(zipFilePath) > 0, "Zip file should not be empty");

        // Step 2: ComponentB extracts the zip file
        componentB.extractZipFile(zipFilePath.toString(), outputDirPath.toString());

        // Verify extraction created the file
        Path extractedFilePath = outputDirPath.resolve(sourceFilePath.getFileName());
        assertTrue(Files.exists(extractedFilePath), "Extracted file should exist");

        // Step 3: Verify the extracted file matches the original
        boolean filesMatch = componentB.verifyExtractedFile(
                sourceFilePath.toString(),
                extractedFilePath.toString());

        assertTrue(filesMatch, "Extracted file content should match the original file");
    }

    @Test
    void testWithLargeFile() throws IOException {
        // Create a larger test file (1MB)
        Path largeFilePath = tempDir.resolve("largeFile.dat");
        byte[] largeContent = new byte[1024 * 1024]; // 1MB
        // Fill with pseudo-random data
        for (int i = 0; i < largeContent.length; i++) {
            largeContent[i] = (byte) (i % 256);
        }
        Files.write(largeFilePath, largeContent);

        Path largeZipPath = tempDir.resolve("large.zip");
        Path largeOutputPath = tempDir.resolve("largeOutput");
        Files.createDirectories(largeOutputPath);

        // Compress
        componentA.compressFile(largeFilePath.toString(), largeZipPath.toString());

        // Extract
        componentB.extractZipFile(largeZipPath.toString(), largeOutputPath.toString());

        // Verify
        Path extractedLargeFile = largeOutputPath.resolve(largeFilePath.getFileName());
        boolean filesMatch = componentB.verifyExtractedFile(
                largeFilePath.toString(),
                extractedLargeFile.toString());

        assertTrue(filesMatch, "Large file content should match after compression and extraction");
    }

    @Test
    void testMultipleFilesCompressAndExtract() throws IOException {
        // Setup multiple files
        Path file1 = tempDir.resolve("file1.txt");
        Path file2 = tempDir.resolve("file2.txt");
        Files.writeString(file1, "Content of file 1");
        Files.writeString(file2, "Content of file 2");

        Path multiZipPath = tempDir.resolve("multi.zip");
        Path multiOutputPath = tempDir.resolve("multiOutput");
        Files.createDirectories(multiOutputPath);

        // This test would require adapting ComponentA to handle multiple files
        // Since the current implementation only handles single files, we'll test them
        // sequentially

        // File 1
        componentA.compressFile(file1.toString(), multiZipPath.toString());
        componentB.extractZipFile(multiZipPath.toString(), multiOutputPath.toString());
        boolean file1Matches = componentB.verifyExtractedFile(
                file1.toString(),
                multiOutputPath.resolve(file1.getFileName()).toString());

        // File 2 - would overwrite the zip in current implementation
        Path zipFile2 = tempDir.resolve("multi2.zip");
        componentA.compressFile(file2.toString(), zipFile2.toString());
        componentB.extractZipFile(zipFile2.toString(), multiOutputPath.toString());
        boolean file2Matches = componentB.verifyExtractedFile(
                file2.toString(),
                multiOutputPath.resolve(file2.getFileName()).toString());

        assertTrue(file1Matches, "File 1 should match after compression and extraction");
        assertTrue(file2Matches, "File 2 should match after compression and extraction");
    }

    @Test
    void testErrorHandling() {
        // Test with non-existent source file
        Path nonExistentFile = tempDir.resolve("doesNotExist.txt");
        Path errorZipPath = tempDir.resolve("error.zip");

        // ComponentA should throw IOException when source file doesn't exist
        Exception exception = assertThrows(IOException.class, () -> {
            componentA.compressFile(nonExistentFile.toString(), errorZipPath.toString());
        });

        assertNotNull(exception, "Exception should be thrown for non-existent file");
    }
}