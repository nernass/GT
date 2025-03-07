import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    private static final String SECRET_KEY = "MySuperSecretKey";

    @TempDir
    Path tempDir;
    private Path originalFile;
    private Path encryptedFile;
    private Path decryptedFile;

    @BeforeEach
    void setUp() throws IOException {
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Create test files
        originalFile = tempDir.resolve("original.txt");
        encryptedFile = tempDir.resolve("encrypted.txt");
        decryptedFile = tempDir.resolve("decrypted.txt");

        // Write test content
        Files.writeString(originalFile, "This is a test content\nWith multiple lines\nTo verify encryption");
    }

    @Test
    void testFileEncryptionAndDecryption() throws Exception {
        // Encrypt the file
        componentA.encryptFile(
                originalFile.toString(),
                encryptedFile.toString(),
                SECRET_KEY);

        assertTrue(Files.exists(encryptedFile), "Encrypted file should exist");
        assertNotEquals(
                Files.readString(originalFile),
                Files.readString(encryptedFile),
                "Encrypted content should differ from original");

        // Decrypt the file
        componentB.decryptFile(
                encryptedFile.toString(),
                decryptedFile.toString(),
                SECRET_KEY);

        assertTrue(Files.exists(decryptedFile), "Decrypted file should exist");
        assertTrue(
                componentB.verifyDecryption(originalFile.toString(), decryptedFile.toString()),
                "Decrypted content should match original");
    }

    @Test
    void testEncryptionWithInvalidKey() {
        assertThrows(Exception.class, () -> {
            componentA.encryptFile(
                    originalFile.toString(),
                    encryptedFile.toString(),
                    "" // Empty key should cause an exception
            );
        });
    }

    @Test
    void testDecryptionWithWrongKey() {
        assertThrows(Exception.class, () -> {
            // First encrypt with correct key
            componentA.encryptFile(
                    originalFile.toString(),
                    encryptedFile.toString(),
                    SECRET_KEY);

            // Try to decrypt with wrong key
            componentB.decryptFile(
                    encryptedFile.toString(),
                    decryptedFile.toString(),
                    "WrongKey123456789" // Different key should cause an exception
            );
        });
    }

    @Test
    void testWithNonExistentInputFile() {
        Path nonExistentFile = tempDir.resolve("nonexistent.txt");
        assertThrows(IOException.class, () -> {
            componentA.encryptFile(
                    nonExistentFile.toString(),
                    encryptedFile.toString(),
                    SECRET_KEY);
        });
    }

    @AfterEach
    void cleanUp() throws IOException {
        // Clean up test files if they exist
        Files.deleteIfExists(encryptedFile);
        Files.deleteIfExists(decryptedFile);
    }
}