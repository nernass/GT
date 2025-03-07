import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ComponentIntegrationTest {

    @Mock
    private ComponentA componentA;

    @Mock
    private ComponentB componentB;

    @TempDir
    File tempDir;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSuccessPath() throws IOException {
        // Arrange
        String sourceFile = tempDir.getAbsolutePath() + "/source.txt";
        String zipFile = tempDir.getAbsolutePath() + "/archive.zip";
        String outputDir = tempDir.getAbsolutePath() + "/output";

        // Create a sample file to compress
        File file = new File(sourceFile);
        file.createNewFile();

        // Act
        componentA.compressFile(sourceFile, zipFile);
        componentB.extractZipFile(zipFile, outputDir);

        // Assert
        File extractedFile = new File(outputDir + "/source.txt");
        assertTrue(extractedFile.exists());
    }

    @Test
    public void testComponentBFailure() throws IOException {
        // Arrange
        String sourceFile = tempDir.getAbsolutePath() + "/source.txt";
        String zipFile = tempDir.getAbsolutePath() + "/archive.zip";
        String outputDir = tempDir.getAbsolutePath() + "/output";

        // Create a sample file to compress
        File file = new File(sourceFile);
        file.createNewFile();

        doThrow(new IOException("Extraction failed")).when(componentB).extractZipFile(zipFile, outputDir);

        // Act & Assert
        componentA.compressFile(sourceFile, zipFile);
        assertThrows(IOException.class, () -> componentB.extractZipFile(zipFile, outputDir));
    }

    @Test
    public void testInvalidInputToComponentA() {
        // Arrange
        String invalidSourceFile = tempDir.getAbsolutePath() + "/nonexistent.txt";
        String zipFile = tempDir.getAbsolutePath() + "/archive.zip";

        // Act & Assert
        assertThrows(IOException.class, () -> componentA.compressFile(invalidSourceFile, zipFile));
    }
}