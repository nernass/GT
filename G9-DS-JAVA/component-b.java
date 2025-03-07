// ComponentB.java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ComponentB {

    /**
     * Reads the last line of the log file.
     *
     * @param logFile The path to the log file.
     * @return The last line of the log file.
     * @throws IOException If an I/O error occurs during the read process.
     */
    public String readLastLine(String logFile) throws IOException {
        String lastLine = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lastLine = line;
            }
        }
        return lastLine;
    }

    /**
     * Verifies that the last line of the log file matches the expected data.
     *
     * @param logFile      The path to the log file.
     * @param expectedData The expected data to verify.
     * @return True if the last line matches the expected data, false otherwise.
     * @throws IOException If an I/O error occurs during the verification process.
     */
    public boolean verifyLastLine(String logFile, String expectedData) throws IOException {
        String lastLine = readLastLine(logFile);
        return lastLine.equals(expectedData);
    }
}