
// ComponentB.java
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class ComponentB {

    /**
     * Restores the backup by copying files from the backup directory to the restore
     * directory.
     *
     * @param backupDir  The path to the backup directory.
     * @param restoreDir The path to the restore directory.
     * @throws IOException If an I/O error occurs during the restore process.
     */
    public void restoreBackup(String backupDir, String restoreDir) throws IOException {
        Path backupPath = Paths.get(backupDir);
        Path restorePath = Paths.get(restoreDir);

        // Create the restore directory if it doesn't exist
        if (!Files.exists(restorePath)) {
            Files.createDirectories(restorePath);
        }

        // Copy all files from the backup directory to the restore directory
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(backupPath)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    Path targetFile = restorePath.resolve(file.getFileName());
                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    /**
     * Verifies that the files in the restore directory match the files in the
     * source directory.
     *
     * @param sourceDir  The path to the source directory.
     * @param restoreDir The path to the restore directory.
     * @return True if all files match, false otherwise.
     * @throws IOException If an I/O error occurs during the verification process.
     */
    public boolean verifyRestore(String sourceDir, String restoreDir) throws IOException {
        Path sourcePath = Paths.get(sourceDir);
        Path restorePath = Paths.get(restoreDir);

        // Get the list of files in the source directory
        Set<String> sourceFiles = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcePath)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    sourceFiles.add(file.getFileName().toString());
                }
            }
        }

        // Get the list of files in the restore directory
        Set<String> restoreFiles = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(restorePath)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    restoreFiles.add(file.getFileName().toString());
                }
            }
        }

        // Verify that the files match
        return sourceFiles.equals(restoreFiles);
    }
}