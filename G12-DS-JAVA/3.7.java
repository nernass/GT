egrationTest.java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {
    
    private ComponentA componentA;
    private ComponentB componentB;
    private String testContent = "This is a test content for integration testing.";
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }
    
    @Test
    void testCompressAndExtract() throws IOException {
        // Create source file with test content
        Path sourceFile = tempDir.resolve("source.txt");
        Files.write(sourceFile, testContent.getBytes(StandardCharsets.UTF_8));
        
        // Define zip file path
        String zipFilePath = tempDir.resolve("archive.zip").toString();
        
        // Define extraction directory
        Path extractionDir = tempDir.resolve("extracted");
        Files.createDirectories(extractionDir);
        
        // Compress file using ComponentA
        componentA.compressFile(sourceFile.toString(), zipFilePath);
        
        // Verify zip file was created
        File zipFile = new File(zipFilePath);
        assertTrue(zipFile.exists(), "ZIP file should be created");
        assertTrue(zipFile.length() > 0, "ZIP file should not be empty");
        
        // Extract file using ComponentB
        componentB.extractZipFile(zipFilePath, extractionDir.toString());
        
        // Verify file was extracted correctly
        Path extractedFile = extractionDir.resolve("source.txt");
        assertTrue(Files.exists(extractedFile), "Extracted file should exist");
        
        // Verify content of extracted file
        String extractedContent = Files.readString(extractedFile, StandardCharsets.UTF_8);
        assertEquals(testContent, extractedContent, "Extracted content should match original content");
        
        // Verify using ComponentB's verification method
        boolean filesMatch = componentB.verifyExtractedFile(
            sourceFile.toString(), 
            extractedFile.toString()
        );
        assertTrue(filesMatch, "ComponentB verification should confirm files match");
    }
    
    @Test
    void testErrorHandling() {
        // Test with non-existent source file
        String nonExistentFile = tempDir.resolve("non-existent.txt").toString();
        String zipFilePath = tempDir.resolve("error.zip").toString();
        
        // ComponentA should throw exception for non-existent file
        IOException compressException = assertThrows(IOException.class, 
            () -> componentA.compressFile(nonExistentFile, zipFilePath));
        assertNotNull(compressException, "Exception should be thrown for non-existent source file");
        
        // Test with non-existent zip file
        String extractionDir = tempDir.resolve("error-extract").toString();
        IOException extractException = assertThrows(IOException.class, 
            () -> componentB.extractZipFile(nonExistentFile, extractionDir));
        assertNotNull(extractException, "Exception should be thrown for non-existent zip file");
    }
    
    @Test
    void testWithLargeFile() throws IOException {
        // Create a larger file for testing
        Path largeFile = tempDir.resolve("large.dat");
        byte[] largeContent = new byte[1024 * 1024]; // 1MB of data
        new java.util.Random().nextBytes(largeContent);
        Files.write(largeFile, largeContent);
        
        // Compress and extract large file
        String zipFilePath = tempDir.resolve("large.zip").toString();
        Path extractionDir = tempDir.resolve("large-extracted");
        Files.createDirectories(extractionDir);
        
        componentA.compressFile(largeFile.toString(), zipFilePath);
        componentB.extractZipFile(zipFilePath, extractionDir.toString());
        
        // Verify extracted file
        Path extractedLargeFile = extractionDir.resolve("large.dat");
        assertTrue(Files.exists(extractedLargeFile), "Large extracted file should exist");
        
        // Verify content using ComponentB's method
        boolean filesMatch = componentB.verifyExtractedFile(
            largeFile.toString(), 
            extractedLargeFile.toString()
        );
        assertTrue(filesMatch, "Large file contents should match after compression and extraction");
    }
}