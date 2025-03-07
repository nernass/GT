import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    public void testCompressAndExtract(@TempDir Path tempDir) {
        String originalFileName = "testfile.txt";
        String zipFileName = "testfile.zip";
        String extractedFileName = "extracted_testfile.txt";

        Path originalFile = tempDir.resolve(originalFileName);
        Path zipFile = tempDir.resolve(zipFileName);
        Path extractedFile = tempDir.resolve(extractedFileName);

        try {
            // Create a test file
            Files.writeString(originalFile, "This is a test file.");

            // Compress the file
            componentA.compressFile(originalFile.toString(), zipFile.toString());

            // Extract the file
            componentB.extractZipFile(zipFile.toString(), tempDir.toString());

            // Verify the extracted file
            assertTrue(componentB.verifyExtractedFile(originalFile.toString(), extractedFile.toString()));
        } catch (IOException e) {
            fail("IOException occurred: " + e.getMessage());
        }
    }
}