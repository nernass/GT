import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    public void testEncryptionDecryption(@TempDir Path tempDir) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        String secretKey = "1234567890123456"; // 16-byte key for AES

        Path originalFile = tempDir.resolve("original.txt");
        Path encryptedFile = tempDir.resolve("encrypted.txt");
        Path decryptedFile = tempDir.resolve("decrypted.txt");

        // Write some content to the original file
        Files.writeString(originalFile, "This is a test content.");

        // Encrypt the file
        componentA.encryptFile(originalFile.toString(), encryptedFile.toString(), secretKey);

        // Decrypt the file
        componentB.decryptFile(encryptedFile.toString(), decryptedFile.toString(), secretKey);

        // Verify the decrypted content matches the original content
        assertTrue(componentB.verifyDecryption(originalFile.toString(), decryptedFile.toString()));
    }

    @Test
    public void testInvalidKey(@TempDir Path tempDir) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException {
        String secretKey = "1234567890123456"; // 16-byte key for AES
        String invalidKey = "invalidkey123456"; // Invalid key

        Path originalFile = tempDir.resolve("original.txt");
        Path encryptedFile = tempDir.resolve("encrypted.txt");

        // Write some content to the original file
        Files.writeString(originalFile, "This is a test content.");

        // Encrypt the file
        componentA.encryptFile(originalFile.toString(), encryptedFile.toString(), secretKey);

        // Attempt to decrypt the file with an invalid key and expect an InvalidKeyException
        assertThrows(InvalidKeyException.class, () -> {
            componentB.decryptFile(encryptedFile.toString(), originalFile.toString(), invalidKey);
        });
    }
}