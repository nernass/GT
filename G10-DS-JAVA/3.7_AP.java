java
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ComponentIntegrationTest {

    @Mock
    private DataSource dataSource;
    
    @Mock
    private JdbcTemplate jdbcTemplate;
    
    private ComponentA componentA;
    private ComponentB componentB;
    
    private AutoCloseable closeable;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        
        // Set up mock for JdbcTemplate
        when(dataSource.toString()).thenReturn("MockDataSource");
        componentA = new ComponentA(dataSource);
        
        // Use reflection to replace the jdbcTemplate in ComponentA with our mock
        try {
            java.lang.reflect.Field field = ComponentA.class.getDeclaredField("jdbcTemplate");
            field.setAccessible(true);
            field.set(componentA, jdbcTemplate);
        } catch (Exception e) {
            fail("Failed to set mock jdbcTemplate: " + e.getMessage());
        }
        
        componentB = new ComponentB();
    }
    
    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }
    
    @Test
    public void testSuccessfulReportGenerationAndSaving() throws IOException {
        // Setup mock database response
        List<Map<String, Object>> mockSalesData = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("product", "Product A");
        row1.put("quantity", 5);
        row1.put("price", 10.99);
        mockSalesData.add(row1);
        
        Map<String, Object> row2 = new HashMap<>();
        row2.put("product", "Product B");
        row2.put("quantity", 3);
        row2.put("price", 24.99);
        mockSalesData.add(row2);
        
        when(jdbcTemplate.queryForList(anyString())).thenReturn(mockSalesData);
        
        // Define file path for the report
        String filePath = tempDir.resolve("sales_report.txt").toString();
        
        // Step 1: Generate report using ComponentA
        String generatedReport = componentA.generateReport();
        
        // Step 2: Save report using ComponentB
        componentB.saveReportToFile(generatedReport, filePath);
        
        // Step 3: Verify report content using ComponentB
        boolean verified = componentB.verifyReportContent(filePath, generatedReport);
        
        // Assertions
        assertTrue(verified, "Report content verification should succeed");
        verify(jdbcTemplate, times(1)).queryForList(anyString());
        
        // Check the actual content of the file
        String actualFileContent = new String(Files.readAllBytes(Path.of(filePath)));
        String expectedContent = "Sales Report:\n" +
                                 "---------------------------------\n" +
                                 "Product: Product A, Quantity: 5, Price: 10.99\n" +
                                 "Product: Product B, Quantity: 3, Price: 24.99\n" +
                                 "---------------------------------\n";
        assertEquals(expectedContent, actualFileContent, "File content should match expected report");
    }
    
    @Test
    public void testEmptySalesData() throws IOException {
        // Setup empty database response
        when(jdbcTemplate.queryForList(anyString())).thenReturn(new ArrayList<>());
        
        // Define file path for the report
        String filePath = tempDir.resolve("empty_report.txt").toString();
        
        // Generate report with empty data
        String generatedReport = componentA.generateReport();
        
        // Save and verify the empty report
        componentB.saveReportToFile(generatedReport, filePath);
        boolean verified = componentB.verifyReportContent(filePath, generatedReport);
        
        // Assertions
        assertTrue(verified, "Empty report verification should succeed");
        verify(jdbcTemplate, times(1)).queryForList(anyString());
        
        // Check the actual content of the file - should be just the header and footer
        String actualFileContent = new String(Files.readAllBytes(Path.of(filePath)));
        String expectedContent = "Sales Report:\n" +
                                 "---------------------------------\n" +
                                 "---------------------------------\n";
        assertEquals(expectedContent, actualFileContent, "Empty report should contain only headers and footers");
    }
    
    @Test
    public void testDatabaseFailure() {
        // Simulate database error
        when(jdbcTemplate.queryForList(anyString())).thenThrow(new RuntimeException("Database connection failed"));
        
        // Define file path for the report
        String filePath = tempDir.resolve("error_report.txt").toString();
        
        // Try to generate and save report
        Exception exception = assertThrows(RuntimeException.class, () -> {
            String report = componentA.generateReport();
            componentB.saveReportToFile(report, filePath);
        });
        
        // Verify exception message contains the expected error
        assertTrue(exception.getMessage().contains("Database connection failed"));
        
        // Verify the file was not created
        assertFalse(Files.exists(Path.of(filePath)), "File should not be created when database fails");
    }
    
    @Test
    public void testFileWriteFailure() throws IOException {
        // Setup mock database response
        List<Map<String, Object>> mockSalesData = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("product", "Test Product");
        row.put("quantity", 1);
        row.put("price", 9.99);
        mockSalesData.add(row);
        when(jdbcTemplate.queryForList(anyString())).thenReturn(mockSalesData);
        
        // Use an invalid file path to cause a write error
        String invalidFilePath = "/non-existent-directory/test-report.txt";
        
        // Generate report successfully
        String generatedReport = componentA.generateReport();
        
        // Try to save to invalid path
        Exception exception = assertThrows(IOException.class, () -> {
            componentB.saveReportToFile(generatedReport, invalidFilePath);
        });
        
        // Verify that database was queried
        verify(jdbcTemplate, times(1)).queryForList(anyString());
    }
    
    @Test
    public void testContentVerificationFailure() throws IOException {
        // Setup mock database response
        List<Map<String, Object>> mockSalesData = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("product", "Test Product");
        row.put("quantity", 1);
        row.put("price", 9.99);
        mockSalesData.add(row);
        when(jdbcTemplate.queryForList(anyString())).thenReturn(mockSalesData);
        
        String filePath = tempDir.resolve("tampered_report.txt").toString();
        
        // Generate report
        String generatedReport = componentA.generateReport();
        
        // Save report
        componentB.saveReportToFile(generatedReport, filePath);
        
        // Tamper with the file content
        Files.writeString(Path.of(filePath), "Tampered content");
        
        // Verification should fail
        boolean verified = componentB.verifyReportContent(filePath, generatedReport);
        
        assertFalse(verified, "Verification should fail when file content is modified");
    }
}