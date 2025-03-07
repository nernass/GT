import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
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

    @TempDir
    Path tempDir;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        // Set up the mock JdbcTemplate
        when(dataSource.getConnection()).thenReturn(null); // Not used directly in our test
        componentA = new ComponentA(dataSource);

        // Inject the mock JdbcTemplate into ComponentA using reflection
        try {
            java.lang.reflect.Field field = ComponentA.class.getDeclaredField("jdbcTemplate");
            field.setAccessible(true);
            field.set(componentA, jdbcTemplate);
        } catch (Exception e) {
            fail("Failed to inject mock JdbcTemplate: " + e.getMessage());
        }

        componentB = new ComponentB();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testSuccessfulReportGenerationAndSaving() throws IOException {
        // Prepare mock data for the database query
        List<Map<String, Object>> mockSalesData = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("product", "Laptop");
        row1.put("quantity", 5);
        row1.put("price", 1200.00);
        mockSalesData.add(row1);

        Map<String, Object> row2 = new HashMap<>();
        row2.put("product", "Mouse");
        row2.put("quantity", 10);
        row2.put("price", 25.50);
        mockSalesData.add(row2);

        // Configure the mock JdbcTemplate to return our test data
        when(jdbcTemplate.queryForList(anyString())).thenReturn(mockSalesData);

        // Generate the report using ComponentA
        String report = componentA.generateReport();

        // Verify that the report has been generated correctly
        assertNotNull(report);
        assertTrue(report.contains("Laptop"));
        assertTrue(report.contains("Mouse"));

        // Save the report to a file using ComponentB
        String filePath = tempDir.resolve("report.txt").toString();
        componentB.saveReportToFile(report, filePath);

        // Verify that the file exists and contains the correct content
        assertTrue(Files.exists(Path.of(filePath)));

        // Use ComponentB to verify the report content
        boolean verificationResult = componentB.verifyReportContent(filePath, report);
        assertTrue(verificationResult);

        // Verify interactions
        verify(jdbcTemplate).queryForList(anyString());
    }

    @Test
    void testEmptyReportGenerationAndSaving() throws IOException {
        // Configure the mock JdbcTemplate to return empty data
        when(jdbcTemplate.queryForList(anyString())).thenReturn(new ArrayList<>());

        // Generate the report using ComponentA
        String report = componentA.generateReport();

        // Verify that an empty report has been generated
        assertNotNull(report);
        assertEquals("Sales Report:\n---------------------------------\n---------------------------------\n", report);

        // Save the report to a file using ComponentB
        String filePath = tempDir.resolve("empty_report.txt").toString();
        componentB.saveReportToFile(report, filePath);

        // Verify that the file exists and contains the correct content
        assertTrue(Files.exists(Path.of(filePath)));

        // Use ComponentB to verify the report content
        boolean verificationResult = componentB.verifyReportContent(filePath, report);
        assertTrue(verificationResult);
    }

    @Test
    void testExceptionHandlingDuringFileOperations() throws IOException {
        // Prepare mock data
        List<Map<String, Object>> mockSalesData = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("product", "Keyboard");
        row.put("quantity", 15);
        row.put("price", 45.99);
        mockSalesData.add(row);

        // Configure the mock
        when(jdbcTemplate.queryForList(anyString())).thenReturn(mockSalesData);

        // Generate the report
        String report = componentA.generateReport();

        // Attempt to save to an invalid location to trigger an IOException
        String invalidPath = "/invalid/directory/path/report.txt";

        // Assert that the expected exception is thrown
        assertThrows(IOException.class, () -> {
            componentB.saveReportToFile(report, invalidPath);
        });

        // Verify the jdbcTemplate was called
        verify(jdbcTemplate).queryForList(anyString());
    }

    @Test
    void testReportContentMismatch() throws IOException {
        // Configure the mock JdbcTemplate to return data
        List<Map<String, Object>> mockSalesData = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("product", "Tablet");
        row.put("quantity", 3);
        row.put("price", 349.99);
        mockSalesData.add(row);

        when(jdbcTemplate.queryForList(anyString())).thenReturn(mockSalesData);

        // Generate the report
        String report = componentA.generateReport();

        // Save the report
        String filePath = tempDir.resolve("modified_report.txt").toString();
        componentB.saveReportToFile(report, filePath);

        // Verify with a modified report string
        String modifiedReport = report + "Additional content";
        boolean verificationResult = componentB.verifyReportContent(filePath, modifiedReport);

        // The verification should fail because the content doesn't match
        assertFalse(verificationResult);
    }
}