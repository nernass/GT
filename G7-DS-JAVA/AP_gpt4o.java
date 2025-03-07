import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    public void testSuccessPath()
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        String secretKey = "1234567890123456";
        String inputFile = "input.txt";
        String encryptedFile = "encrypted.txt";
        String decryptedFile = "decrypted.txt";

        // Encrypt the file
        componentA.encryptFile(inputFile, encryptedFile, secretKey);

        // Decrypt the file
        componentB.decryptFile(encryptedFile, decryptedFile, secretKey);

        // Verify the decrypted file matches the original
        assertTrue(componentB.verifyDecryption(inputFile, decryptedFile));
    }

    @Test
    public void testComponentBFailure()
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        String secretKey = "1234567890123456";
        String inputFile = "input.txt";
        String encryptedFile = "encrypted.txt";
        String decryptedFile = "decrypted.txt";

        // Encrypt the file
        componentA.encryptFile(inputFile, encryptedFile, secretKey);

        // Mock ComponentB to throw an exception during decryption
        ComponentB mockComponentB = Mockito.spy(componentB);
        doThrow(new InvalidKeyException("Invalid Key")).when(mockComponentB).decryptFile(anyString(), anyString(),
                anyString());

        // Verify that the exception is thrown
        assertThrows(InvalidKeyException.class, () -> {
            mockComponentB.decryptFile(encryptedFile, decryptedFile, secretKey);
        });
    }

    @Test
    public void testInvalidInputToComponentA() {
        String secretKey = "1234567890123456";
        String inputFile = "nonexistent.txt";
        String encryptedFile = "encrypted.txt";

        // Verify that an IOException is thrown for a nonexistent input file
        assertThrows(IOException.class, () -> {
            componentA.encryptFile(inputFile, encryptedFile, secretKey);
        });
    }
}