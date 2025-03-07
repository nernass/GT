import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;

public class ComponentIntegrationTest {

    private static final String TEST_DIR = "test_files";
    private static final String ORIGINAL_FILE = TEST_DIR + "/original.txt";
    private static final String ENCRYPTED_FILE = TEST_DIR + "/encrypted.bin";
    private static final String DECRYPTED_FILE = TEST_DIR + "/decrypted.txt";
    private static final String SECRET_KEY = "ThisIsASecretKey";
    private static final String WRONG_KEY = "ThisIsAWrongKey!";

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeAll
    static void setupTestDirectory() {
        File directory = new File(TEST_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    @BeforeEach
    void setup() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @AfterEach
    void cleanup() {
        // Clean up test files
        deleteFile(ORIGINAL_FILE);
        deleteFile(ENCRYPTED_FILE);
        deleteFile(DECRYPTED_FILE);
    }

    @AfterAll
    static void cleanupTestDirectory() {
        new File(TEST_DIR).delete();
    }

    @Test
    @DisplayName("Test successful encryption and decryption flow")
    void testSuccessfulEncryptionDecryption() throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        // Create test file with content
        String testContent = "This is a test content for encryption and decryption.\n" +
                "It contains multiple lines to test line-by-line verification.";
        createTestFile(ORIGINAL_FILE, testContent);

        // Encrypt file using ComponentA
        componentA.encryptFile(ORIGINAL_FILE, ENCRYPTED_FILE, SECRET_KEY);

        // Verify encrypted file exists and is different from original
        File encrypted = new File(ENCRYPTED_FILE);
        assertTrue(encrypted.exists(), "Encrypted file should exist");
        assertFalse(filesAreIdentical(ORIGINAL_FILE, ENCRYPTED_FILE),
                "Encrypted file should be different from original");

        // Decrypt file using ComponentB
        componentB.decryptFile(ENCRYPTED_FILE, DECRYPTED_FILE, SECRET_KEY);

        // Verify decryption using ComponentB's verification method
        boolean verificationResult = componentB.verifyDecryption(ORIGINAL_FILE, DECRYPTED_FILE);
        assertTrue(verificationResult, "Decrypted content should match the original");
    }

    @Test
    @DisplayName("Test decryption fails with wrong key")
    void testDecryptionWithWrongKey() throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        // Create test file with content
        String testContent = "This is a test content for encryption and decryption.";
        createTestFile(ORIGINAL_FILE, testContent);

        // Encrypt file using ComponentA
        componentA.encryptFile(ORIGINAL_FILE, ENCRYPTED_FILE, SECRET_KEY);

        // Decrypt file using ComponentB with wrong key
        componentB.decryptFile(ENCRYPTED_FILE, DECRYPTED_FILE, WRONG_KEY);

        // Verification should fail
        boolean verificationResult = componentB.verifyDecryption(ORIGINAL_FILE, DECRYPTED_FILE);
        assertFalse(verificationResult, "Decryption with wrong key should not match original");
    }

    @Test
    @DisplayName("Test encrypting and decrypting empty file")
    void testEmptyFile() throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        // Create empty test file
        createTestFile(ORIGINAL_FILE, "");

        // Encrypt empty file
        componentA.encryptFile(ORIGINAL_FILE, ENCRYPTED_FILE, SECRET_KEY);

        // Decrypt file
        componentB.decryptFile(ENCRYPTED_FILE, DECRYPTED_FILE, SECRET_KEY);

        // Verify empty decryption
        boolean verificationResult = componentB.verifyDecryption(ORIGINAL_FILE, DECRYPTED_FILE);
        assertTrue(verificationResult, "Empty file should decrypt correctly");
    }

    @Test
    @DisplayName("Test encrypting and decrypting large file")
    void testLargeFile() throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {
        // Create large test file (100KB)
        StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 2000; i++) {
            largeContent.append("Line ").append(i).append(": This is test content to make a larger file.\n");
        }
        createTestFile(ORIGINAL_FILE, largeContent.toString());

        // Encrypt large file
        componentA.encryptFile(ORIGINAL_FILE, ENCRYPTED_FILE, SECRET_KEY);

        // Decrypt large file
        componentB.decryptFile(ENCRYPTED_FILE, DECRYPTED_FILE, SECRET_KEY);

        // Verify decryption
        boolean verificationResult = componentB.verifyDecryption(ORIGINAL_FILE, DECRYPTED_FILE);
        assertTrue(verificationResult, "Large file should decrypt correctly");
    }

    // Helper methods

    private void createTestFile(String filePath, String content) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        }
    }

    private void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    private boolean filesAreIdentical(String filePath1, String filePath2) {
        try {
            byte[] file1Content = Files.readAllBytes(new File(filePath1).toPath());
            byte[] file2Content = Files.readAllBytes(new File(filePath2).toPath());
            return java.util.Arrays.equals(file1Content, file2Content);
        } catch (IOException e) {
            return false;
        }
    }
}