// ComponentB.java
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class ComponentB {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    /**
     * Decrypts a file using AES decryption.
     *
     * @param inputFile  The path to the encrypted input file.
     * @param outputFile The path to the decrypted output file.
     * @param secretKey  The secret key for decryption.
     * @throws IOException              If an I/O error occurs.
     * @throws NoSuchAlgorithmException If the decryption algorithm is not available.
     * @throws NoSuchPaddingException   If the padding scheme is not available.
     * @throws InvalidKeyException      If the secret key is invalid.
     */
    public void decryptFile(String inputFile, String outputFile, String secretKey)
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        // Convert the secret key to a Key object
        SecretKey key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);

        // Initialize the cipher for decryption
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);

        // Decrypt the file
        try (FileInputStream inputStream = new FileInputStream(inputFile);
             CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = cipherInputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * Verifies that the decrypted file content matches the original content.
     *
     * @param originalFile The path to the original file.
     * @param decryptedFile The path to the decrypted file.
     * @return True if the content matches, false otherwise.
     * @throws IOException If an I/O error occurs.
     */
    public boolean verifyDecryption(String originalFile, String decryptedFile) throws IOException {
        try (BufferedReader originalReader = new BufferedReader(new FileReader(originalFile));
             BufferedReader decryptedReader = new BufferedReader(new FileReader(decryptedFile))) {
            String originalLine;
            String decryptedLine;
            while ((originalLine = originalReader.readLine()) != null) {
                decryptedLine = decryptedReader.readLine();
                if (!originalLine.equals(decryptedLine)) {
                    return false;
                }
            }
            // Ensure the decrypted file doesn't have extra lines
            return decryptedReader.readLine() == null;
        }
    }
}