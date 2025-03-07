// ComponentA.java
import java.io.IOException;
import java.nio.file.*;

public class ComponentA {

    /**
     * Backs up a directory by copying all files from the source directory to the backup directory.
     *
     * @param sourceDir  The path to the source directory.
     * @param backupDir  The path to the backup directory.
     * @throws IOException If an I/O error occurs during the backup process.
     */
    public void backupDirectory(String sourceDir, String backupDir) throws IOException {
        Path sourcePath = Paths.get(sourceDir);
        Path backupPath = Paths.get(backupDir);

        // Create the backup directory if it doesn't exist
        if (!Files.exists(backupPath)) {
            Files.createDirectories(backupPath);
        }

        // Copy all files from the source directory to the backup directory
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcePath)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    Path targetFile = backupPath.resolve(file.getFileName());
                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}