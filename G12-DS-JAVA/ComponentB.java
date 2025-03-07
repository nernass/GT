
// ComponentB.java
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ComponentB {

    /**
     * Extracts a ZIP archive to a specified directory.
     *
     * @param zipFile   The path to the ZIP archive.
     * @param outputDir The path to the output directory.
     * @throws IOException If an I/O error occurs during the extraction process.
     */
    public void extractZipFile(String zipFile, String outputDir) throws IOException {
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry = zipIn.getNextEntry();

            // Extract each entry in the ZIP archive
            while (entry != null) {
                String filePath = outputDir + "/" + entry.getName();
                if (!entry.isDirectory()) {
                    // Create parent directories if they don't exist
                    new File(filePath).getParentFile().mkdirs();

                    // Write the file content
                    try (FileOutputStream fos = new FileOutputStream(filePath)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = zipIn.read(buffer)) >= 0) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }

    /**
     * Verifies that the extracted file matches the original file.
     *
     * @param originalFile  The path to the original file.
     * @param extractedFile The path to the extracted file.
     * @return True if the files match, false otherwise.
     * @throws IOException If an I/O error occurs during the verification process.
     */
    public boolean verifyExtractedFile(String originalFile, String extractedFile) throws IOException {
        byte[] originalContent = Files.readAllBytes(Paths.get(originalFile));
        byte[] extractedContent = Files.readAllBytes(Paths.get(extractedFile));
        return java.util.Arrays.equals(originalContent, extractedContent);
    }
}