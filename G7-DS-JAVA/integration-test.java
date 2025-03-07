// IntegrationTest.java
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    private static final String ORIGINAL_FILE = "test_original.txt";
    private static final String ENCRYPTED_FILE = "test_encrypted.txt";
    private static final String DECRYPTED_FILE = "test_decrypted.txt";
    private static final String SECRET_KEY = "ThisIsASecretKey"; // 16 characters for AES

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() throws IOException {
        // Initialize components
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Create the original file with some test content
        try (FileWriter writer = new FileWriter(ORIGINAL_FILE)) {
            writer.write("This is a test file for encryption and decryption.");
        }
    }

    @AfterEach
    public void tearDown() {
        // Clean up files after each test
        new File(ORIGINAL_FILE).delete();
        new File(ENCRYPTED_FILE).delete();
        new File(DECRYPTED_FILE).delete();
    }

    @Test
    public void testIntegration() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        // Step 1: Encrypt the original file using Component A
        componentA.encryptFile(ORIGINAL_FILE, ENCRYPTED_FILE, SECRET_KEY);

        // Step 2: Decrypt the encrypted file using Component B
        componentB.decryptFile(ENCRYPTED_FILE, DECRYPTED_FILE, SECRET_KEY);

        // Step 3: Verify that the decrypted content matches the original content
        boolean isVerified = componentB.verifyDecryption(ORIGINAL_FILE, DECRYPTED_FILE);
        assertTrue(isVerified, "Decrypted content does not match the original content");
    }
}