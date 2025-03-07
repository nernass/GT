// ComponentB.java
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ComponentB {

    /**
     * Saves the report to a file.
     *
     * @param report   The report content to save.
     * @param filePath The path to the file where the report will be saved.
     * @throws IOException If an I/O error occurs during the save process.
     */
    public void saveReportToFile(String report, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(report);
        }
    }

    /**
     * Verifies that the file content matches the expected report.
     *
     * @param filePath The path to the file to verify.
     * @param expectedReport The expected report content.
     * @return True if the file content matches the expected report, false otherwise.
     * @throws IOException If an I/O error occurs during the verification process.
     */
    public boolean verifyReportContent(String filePath, String expectedReport) throws IOException {
        String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
        return fileContent.equals(expectedReport);
    }
}