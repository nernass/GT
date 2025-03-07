// IntegrationTest.java
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    private static final String ORIGINAL_FILE = "test_original.txt";
    private static final String CLOUD_DIR = "cloud_storage";
    private static final String DOWNLOAD_DIR = "downloads";
    private static final String FILE_NAME = "test_original.txt";

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() throws IOException {
        // Initialize components
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Create the original file with some test content
        try (FileWriter writer = new FileWriter(ORIGINAL_FILE)) {
            writer.write("This is a test file for upload and download.");
        }
    }

    @AfterEach
    public void tearDown() {
        // Clean up files and directories after each test
        new File(ORIGINAL_FILE).delete();
        deleteDirectory(new File(CLOUD_DIR));
        deleteDirectory(new File(DOWNLOAD_DIR));
    }

    @Test
    public void testIntegration() throws IOException {
        // Step 1: Upload the original file to the cloud storage using Component A
        componentA.uploadFile(ORIGINAL_FILE, CLOUD_DIR);

        // Step 2: Download the file from the cloud storage using Component B
        componentB.downloadFile(CLOUD_DIR, FILE_NAME, DOWNLOAD_DIR);

        // Step 3: Verify that the downloaded content matches the original content
        boolean isVerified = componentB.verifyDownload(ORIGINAL_FILE, DOWNLOAD_DIR + File.separator + FILE_NAME);
        assertTrue(isVerified, "Downloaded content does not match the original content");
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