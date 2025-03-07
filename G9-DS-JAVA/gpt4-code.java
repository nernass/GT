import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    private Path logFilePath;

    @BeforeEach
    public void setUp() throws IOException {
        componentA = new ComponentA();
        componentB = new ComponentB();
        logFilePath = Files.createTempFile("logfile", ".txt");
    }

    @Test
    public void testAppendAndVerifyLastLine() throws IOException {
        String data = "Test data";
        componentA.appendToLogFile(logFilePath.toString(), data);
        assertTrue(componentB.verifyLastLine(logFilePath.toString(), data));
    }

    @Test
    public void testVerifyLastLineWithDifferentData() throws IOException {
        String data = "Test data";
        componentA.appendToLogFile(logFilePath.toString(), data);
        assertTrue(!componentB.verifyLastLine(logFilePath.toString(), "Different data"));
    }

    @Test
    public void testIOExceptionHandling() {
        assertThrows(IOException.class, () -> {
            componentA.appendToLogFile("/invalid/path/logfile.txt", "data");
        });
        assertThrows(IOException.class, () -> {
            componentB.readLastLine("/invalid/path/logfile.txt");
        });
    }
}