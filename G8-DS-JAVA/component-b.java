// ComponentB.java
import java.io.IOException;
import java.nio.file.*;

public class ComponentB {

    /**
     * Simulates downloading a file from a cloud storage service.
     *
     * @param cloudDir    The path to the cloud storage directory.
     * @param fileName    The name of the file to download.
     * @param downloadDir The path to the download directory.
     * @throws IOException If an I/O error occurs during the download process.
     */
    public void downloadFile(String cloudDir, String fileName, String downloadDir) throws IOException {
        Path cloudPath = Paths.get(cloudDir, fileName);
        Path downloadPath = Paths.get(downloadDir);

        // Create the download directory if it doesn't exist
        if (!Files.exists(downloadPath)) {
            Files.createDirectories(downloadPath);
        }

        // Copy the file from the cloud directory to the download directory
        Path targetFile = downloadPath.resolve(fileName);
        Files.copy(cloudPath, targetFile, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Verifies that the downloaded file content matches the original file content.
     *
     * @param originalFile The path to the original file.
     * @param downloadedFile The path to the downloaded file.
     * @return True if the content matches, false otherwise.
     * @throws IOException If an I/O error occurs during the verification process.
     */
    public boolean verifyDownload(String originalFile, String downloadedFile) throws IOException {
        byte[] originalContent = Files.readAllBytes(Paths.get(originalFile));
        byte[] downloadedContent = Files.readAllBytes(Paths.get(downloadedFile));
        return java.util.Arrays.equals(originalContent, downloadedContent);
    }
}