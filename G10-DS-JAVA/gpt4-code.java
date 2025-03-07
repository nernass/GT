import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    @BeforeEach
    public void setUp() {
        dataSource = mock(DataSource.class);
        jdbcTemplate = mock(JdbcTemplate.class);
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB();
        componentA.jdbcTemplate = jdbcTemplate;
    }

    @Test
    public void testGenerateAndSaveReport() throws IOException {
        // Mock database data
        Map<String, Object> row1 = new HashMap<>();
        row1.put("product", "Product1");
        row1.put("quantity", 10);
        row1.put("price", 100);
        Map<String, Object> row2 = new HashMap<>();
        row2.put("product", "Product2");
        row2.put("quantity", 5);
        row2.put("price", 50);
        List<Map<String, Object>> salesData = Arrays.asList(row1, row2);

        when(jdbcTemplate.queryForList(anyString())).thenReturn(salesData);

        // Generate report
        String report = componentA.generateReport();

        // Save report to file
        String filePath = "test_report.txt";
        componentB.saveReportToFile(report, filePath);

        // Verify report content
        assertTrue(componentB.verifyReportContent(filePath, report));
    }

    @Test
    public void testSaveReportToFileIOException() {
        String report = "Sample Report";
        String invalidFilePath = "/invalid/path/report.txt";

        assertThrows(IOException.class, () -> {
            componentB.saveReportToFile(report, invalidFilePath);
        });
    }
}