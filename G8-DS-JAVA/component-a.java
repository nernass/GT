// ComponentA.java
import java.io.IOException;
import java.nio.file.*;

public class ComponentA {

    /**
     * Simulates uploading a file to a cloud storage service.
     *
     * @param sourceFile The path to the file to upload.
     * @param cloudDir   The path to the cloud storage directory.
     * @throws IOException If an I/O error occurs during the upload process.
     */
    public void uploadFile(String sourceFile, String cloudDir) throws IOException {
        Path sourcePath = Paths.get(sourceFile);
        Path cloudPath = Paths.get(cloudDir);

        // Create the cloud directory if it doesn't exist
        if (!Files.exists(cloudPath)) {
            Files.createDirectories(cloudPath);
        }

        // Copy the file to the cloud directory
        Path targetFile = cloudPath.resolve(sourcePath.getFileName());
        Files.copy(sourcePath, targetFile, StandardCopyOption.REPLACE_EXISTING);
    }
}