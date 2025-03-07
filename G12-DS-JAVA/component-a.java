// ComponentA.java
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ComponentA {

    /**
     * Compresses a file into a ZIP archive.
     *
     * @param sourceFile The path to the file to compress.
     * @param zipFile    The path to the output ZIP archive.
     * @throws IOException If an I/O error occurs during the compression process.
     */
    public void compressFile(String sourceFile, String zipFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zipOut = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(sourceFile)) {

            // Create a ZIP entry for the file
            ZipEntry zipEntry = new ZipEntry(Paths.get(sourceFile).getFileName().toString());
            zipOut.putNextEntry(zipEntry);

            // Write the file content to the ZIP archive
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) >= 0) {
                zipOut.write(buffer, 0, bytesRead);
            }

            // Close the ZIP entry
            zipOut.closeEntry();
        }
    }
}