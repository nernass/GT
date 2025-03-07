import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ComponentIntegrationTest {

    @Mock
    private ComponentA componentA;

    @Mock
    private ComponentB componentB;

    @TempDir
    Path tempDir;

    private Path logFile;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        logFile = tempDir.resolve("test.log");
    }

    @Test
    public void testSuccessPath() throws IOException {
        // Arrange
        String data = "Test log data";
        doNothing().when(componentA).appendToLogFile(logFile.toString(), data);
        when(componentB.readLastLine(logFile.toString())).thenReturn(data);

        // Act
        componentA.appendToLogFile(logFile.toString(), data);
        String lastLine = componentB.readLastLine(logFile.toString());

        // Assert
        assertEquals(data, lastLine);
    }

    @Test
    public void testComponentBFailure() throws IOException {
        // Arrange
        String data = "Test log data";
        doNothing().when(componentA).appendToLogFile(logFile.toString(), data);
        when(componentB.readLastLine(logFile.toString())).thenThrow(new IOException("Read error"));

        // Act & Assert
        componentA.appendToLogFile(logFile.toString(), data);
        assertThrows(IOException.class, () -> componentB.readLastLine(logFile.toString()));
    }

    @Test
    public void testInvalidInputToComponentA() throws IOException {
        // Arrange
        String invalidData = null;
        doThrow(new IOException("Write error")).when(componentA).appendToLogFile(logFile.toString(), invalidData);

        // Act & Assert
        assertThrows(IOException.class, () -> componentA.appendToLogFile(logFile.toString(), invalidData));
    }
}