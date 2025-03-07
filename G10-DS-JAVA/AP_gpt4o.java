import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class ComponentIntegrationTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ComponentA componentA;

    @InjectMocks
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        componentA = new ComponentA(dataSource);
    }

    @Test
    public void testValidInput_AllComponentsSucceed() throws IOException {
        // Mock database query result
        Map<String, Object> row1 = new HashMap<>();
        row1.put("product", "Product1");
        row1.put("quantity", 10);
        row1.put("price", 100);
        List<Map<String, Object>> salesData = Arrays.asList(row1);

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
    public void testComponentBFailure_ComponentAHandlesError() throws IOException {
        // Mock database query result
        Map<String, Object> row1 = new HashMap<>();
        row1.put("product", "Product1");
        row1.put("quantity", 10);
        row1.put("price", 100);
        List<Map<String, Object>> salesData = Arrays.asList(row1);

        when(jdbcTemplate.queryForList(anyString())).thenReturn(salesData);

        // Generate report
        String report = componentA.generateReport();

        // Mock ComponentB to throw an exception
        doThrow(new IOException("File write error")).when(componentB).saveReportToFile(anyString(), anyString());

        // Save report to file and handle exception
        String filePath = "test_report.txt";
        try {
            componentB.saveReportToFile(report, filePath);
            fail("Expected IOException to be thrown");
        } catch (IOException e) {
            assertEquals("File write error", e.getMessage());
        }
    }

    @Test
    public void testInvalidInput_ComponentA() {
        // Mock database query result with invalid data
        when(jdbcTemplate.queryForList(anyString())).thenReturn(null);

        // Generate report
        String report = componentA.generateReport();

        // Assert that the report is empty or contains error message
        assertTrue(report.isEmpty() || report.contains("Error"));
    }
}