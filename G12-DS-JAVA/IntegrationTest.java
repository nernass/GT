
// IntegrationTest.java
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    private static final String SOURCE_FILE = "test_file.txt";
    private static final String ZIP_FILE = "test_archive.zip";
    private static final String OUTPUT_DIR = "extracted_files";

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() throws IOException {
        // Initialize components
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Create the source file with some test content
        try (FileWriter writer = new FileWriter(SOURCE_FILE)) {
            writer.write("This is a test file for compression and extraction.");
        }
    }

    @AfterEach
    public void tearDown() {
        // Clean up files and directories after each test
        new File(SOURCE_FILE).delete();
        new File(ZIP_FILE).delete();
        deleteDirectory(new File(OUTPUT_DIR));
    }

    @Test
    public void testIntegration() throws IOException {
        // Step 1: Compress the source file into a ZIP archive using Component A
        componentA.compressFile(SOURCE_FILE, ZIP_FILE);

        // Step 2: Extract the ZIP archive using Component B
        componentB.extractZipFile(ZIP_FILE, OUTPUT_DIR);

        // Step 3: Verify that the extracted file matches the original file
        String extractedFile = OUTPUT_DIR + "/" + Paths.get(SOURCE_FILE).getFileName().toString();
        boolean isVerified = componentB.verifyExtractedFile(SOURCE_FILE, extractedFile);
        assertTrue(isVerified, "The extracted file does not match the original file");
    }

    /**
     * Deletes a directory and all its contents.
     *
     * @param directory The directory to delete.
     */
    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
}