import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ComponentABIntegrationTest {

    @Mock
    private DataSource mockDataSource;

    @Mock
    private JdbcTemplate mockJdbcTemplate;

    private ComponentA componentA;
    private ComponentB componentB;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up ComponentA with mock DataSource
        componentA = new ComponentA(mockDataSource);

        // Use reflection to replace the jdbcTemplate with our mock
        try {
            java.lang.reflect.Field field = ComponentA.class.getDeclaredField("jdbcTemplate");
            field.setAccessible(true);
            field.set(componentA, mockJdbcTemplate);
        } catch (Exception e) {
            fail("Failed to set mock JdbcTemplate: " + e.getMessage());
        }

        // Set up ComponentB
        componentB = new ComponentB();
    }

    @Test
    void testGenerateAndSaveReport() throws IOException {
        // Prepare mock data
        List<Map<String, Object>> mockSalesData = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("product", "Product A");
        row1.put("quantity", 10);
        row1.put("price", 99.99);
        mockSalesData.add(row1);

        Map<String, Object> row2 = new HashMap<>();
        row2.put("product", "Product B");
        row2.put("quantity", 5);
        row2.put("price", 49.99);
        mockSalesData.add(row2);

        // Configure mock behavior
        when(mockJdbcTemplate.queryForList("SELECT product, quantity, price FROM sales"))
                .thenReturn(mockSalesData);

        // Generate the report using ComponentA
        String generatedReport = componentA.generateReport();

        // Verify report content
        assertTrue(generatedReport.contains("Product A"));
        assertTrue(generatedReport.contains("Product B"));
        assertTrue(generatedReport.contains("10"));
        assertTrue(generatedReport.contains("5"));
        assertTrue(generatedReport.contains("99.99"));
        assertTrue(generatedReport.contains("49.99"));

        // Save the report using ComponentB
        String filePath = tempDir.resolve("report.txt").toString();
        componentB.saveReportToFile(generatedReport, filePath);

        // Verify the saved content
        boolean contentVerified = componentB.verifyReportContent(filePath, generatedReport);
        assertTrue(contentVerified, "The saved report content should match the generated report");
    }

    @Test
    void testErrorHandlingDuringFileSaving() {
        // Generate report with ComponentA
        String mockReport = "Mock report content";
        when(mockJdbcTemplate.queryForList(anyString())).thenReturn(new ArrayList<>());

        // Try to save to an invalid location
        String invalidFilePath = "/invalid/path/file.txt";

        // Verify exception is thrown
        assertThrows(IOException.class, () -> {
            componentB.saveReportToFile(mockReport, invalidFilePath);
        });
    }

    @Test
    void testEmptyReportGeneration() throws IOException {
        // Configure mock to return empty result
        when(mockJdbcTemplate.queryForList(anyString())).thenReturn(new ArrayList<>());

        // Generate report
        String emptyReport = componentA.generateReport();

        // Verify report structure
        assertTrue(emptyReport.contains("Sales Report:"));
        assertTrue(emptyReport.contains("---------------------------------"));

        // Save and verify empty report
        String filePath = tempDir.resolve("empty_report.txt").toString();
        componentB.saveReportToFile(emptyReport, filePath);

        // Verify the saved content
        boolean contentVerified = componentB.verifyReportContent(filePath, emptyReport);
        assertTrue(contentVerified, "The saved empty report content should match the generated report");
    }
}