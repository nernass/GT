// ComponentA.java
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class ComponentA {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    /**
     * Encrypts a file using AES encryption.
     *
     * @param inputFile  The path to the input file.
     * @param outputFile The path to the encrypted output file.
     * @param secretKey  The secret key for encryption.
     * @throws IOException              If an I/O error occurs.
     * @throws NoSuchAlgorithmException If the encryption algorithm is not available.
     * @throws NoSuchPaddingException   If the padding scheme is not available.
     * @throws InvalidKeyException      If the secret key is invalid.
     */
    public void encryptFile(String inputFile, String outputFile, String secretKey)
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        // Convert the secret key to a Key object
        SecretKey key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);

        // Initialize the cipher for encryption
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        // Encrypt the file
        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile);
             CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) >= 0) {
                cipherOutputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}