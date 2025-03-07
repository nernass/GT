// ComponentA.java
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ComponentA {

    /**
     * Appends a new line of data to a log file.
     *
     * @param logFile The path to the log file.
     * @param data    The data to append to the log file.
     * @throws IOException If an I/O error occurs during the append process.
     */
    public void appendToLogFile(String logFile, String data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(data);
            writer.newLine();
        }
    }
}