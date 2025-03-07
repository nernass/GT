.java
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    
    @TempDir
    Path sourceDir;
    
    @TempDir
    Path cloudDir;
    
    @TempDir
    Path downloadDir;
    
    private Path sourceFile;
    private String testContent = "This is test content for integration test";
    private String fileName = "testFile.txt";

    @BeforeEach
    void setUp() throws IOException {
        componentA = new ComponentA();
        componentB = new ComponentB();
        
        // Create test file with content
        sourceFile = sourceDir.resolve(fileName);
        Files.writeString(sourceFile, testContent);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up created files if needed
    }

    @Test
    void testSuccessfulUploadAndDownload() throws IOException {
        // Test the full workflow: upload -> download -> verify
        
        // 1. Upload file using ComponentA
        componentA.uploadFile(sourceFile.toString(), cloudDir.toString());
        
        // Verify file exists in cloud directory
        Path cloudFile = cloudDir.resolve(fileName);
        assertTrue(Files.exists(cloudFile), "File should exist in cloud directory after upload");
        
        // 2. Download file using ComponentB
        componentB.downloadFile(cloudDir.toString(), fileName, downloadDir.toString());
        
        // Verify file exists in download directory
        Path downloadedFile = downloadDir.resolve(fileName);
        assertTrue(Files.exists(downloadedFile), "File should exist in download directory after download");
        
        // 3. Verify content using ComponentB's verification method
        boolean verified = componentB.verifyDownload(sourceFile.toString(), downloadedFile.toString());
        assertTrue(verified, "Downloaded file content should match the source file content");
        
        // Additional verification of file content
        String downloadedContent = Files.readString(downloadedFile);
        assertEquals(testContent, downloadedContent, "File content should match the original");
    }
    
    @Test
    void testNonExistentSourceFile() {
        // Test upload with non-existent source file
        String nonExistentFile = sourceDir.resolve("nonexistent.txt").toString();
        
        // Should throw IOException when source file doesn't exist
        assertThrows(IOException.class, () -> {
            componentA.uploadFile(nonExistentFile, cloudDir.toString());
        });
    }
    
    @Test
    void testDownloadNonExistentFile() {
        // Test download with non-existent cloud file
        String nonExistentFile = "nonexistent.txt";
        
        // Should throw IOException when cloud file doesn't exist
        assertThrows(IOException.class, () -> {
            componentB.downloadFile(cloudDir.toString(), nonExistentFile, downloadDir.toString());
        });
    }
    
    @Test
    void testModifiedFileVerification() throws IOException {
        // 1. Upload file using ComponentA
        componentA.uploadFile(sourceFile.toString(), cloudDir.toString());
        
        // 2. Download file using ComponentB
        componentB.downloadFile(cloudDir.toString(), fileName, downloadDir.toString());
        
        // 3. Modify the downloaded file
        Path downloadedFile = downloadDir.resolve(fileName);
        Files.writeString(downloadedFile, "Modified content");
        
        // 4. Verify content should fail
        boolean verified = componentB.verifyDownload(sourceFile.toString(), downloadedFile.toString());
        assertFalse(verified, "Verification should fail when file content is modified");
    }
    
    @Test
    void testEndToEndWithMultipleFiles() throws IOException {
        // Create multiple test files
        String[] fileNames = {"file1.txt", "file2.txt", "file3.txt"};
        String[] contents = {"Content 1", "Content 2", "Content 3"};
        
        for (int i = 0; i < fileNames.length; i++) {
            Path file = sourceDir.resolve(fileNames[i]);
            Files.writeString(file, contents[i]);
            
            // Upload each file
            componentA.uploadFile(file.toString(), cloudDir.toString());
            
            // Download each file
            componentB.downloadFile(cloudDir.toString(), fileNames[i], downloadDir.toString());
            
            // Verify each download
            Path downloadedFile = downloadDir.resolve(fileNames[i]);
            boolean verified = componentB.verifyDownload(file.toString(), downloadedFile.toString());
            assertTrue(verified, "Downloaded file " + fileNames[i] + " content should match source");
        }
    }
}