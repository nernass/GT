import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ComponentIntegrationTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB();

        // Set up JdbcTemplate mock in ComponentA
        when(dataSource.getConnection()).thenReturn(null);
    }

    @Test
    void testReportGenerationAndStorage(@TempDir Path tempDir) throws Exception {
        // Prepare test data
        List<Map<String, Object>> mockData = Arrays.asList(
                createSaleRow("Product1", 5, 10.99),
                createSaleRow("Product2", 3, 15.99));

        // Mock database query
        when(jdbcTemplate.queryForList(anyString())).thenReturn(mockData);

        // Generate report using ComponentA
        String report = componentA.generateReport();

        // Save report using ComponentB
        String filePath = tempDir.resolve("test_report.txt").toString();
        componentB.saveReportToFile(report, filePath);

        // Verify report content
        assertTrue(componentB.verifyReportContent(filePath, report),
                "Saved report content should match generated report");

        // Verify expected content structure
        assertTrue(report.contains("Sales Report:"));
        assertTrue(report.contains("Product: Product1"));
        assertTrue(report.contains("Quantity: 5"));
        assertTrue(report.contains("Price: 10.99"));
    }

    @Test
    void testErrorHandling(@TempDir Path tempDir) {
        // Mock database error
        when(jdbcTemplate.queryForList(anyString()))
                .thenThrow(new RuntimeException("Database error"));

        // Verify ComponentA handles database error
        assertThrows(RuntimeException.class, () -> componentA.generateReport());

        // Verify ComponentB handles invalid file path
        String invalidPath = tempDir.resolve("non_existent_dir/report.txt").toString();
        assertThrows(IOException.class,
                () -> componentB.saveReportToFile("test", invalidPath));
    }

    private Map<String, Object> createSaleRow(String product, int quantity, double price) {
        Map<String, Object> row = new HashMap<>();
        row.put("product", product);
        row.put("quantity", quantity);
        row.put("price", price);
        return row;
    }
}