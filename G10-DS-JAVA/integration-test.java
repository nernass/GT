// IntegrationTest.java
import org.junit.jupiter.api.*;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    private static final String REPORT_FILE = "sales_report.txt";

    private ComponentA componentA;
    private ComponentB componentB;
    private DataSource dataSource;

    @BeforeEach
    public void setUp() {
        // Set up the database and components
        dataSource = DatabaseConfig.dataSource();
        DatabaseConfig.initializeDatabase(dataSource);
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB();
    }

    @AfterEach
    public void tearDown() {
        // Clean up the report file after each test
        new File(REPORT_FILE).delete();
    }

    @Test
    public void testIntegration() throws IOException {
        // Step 1: Generate a report using Component A
        String report = componentA.generateReport();

        // Step 2: Save the report to a file using Component B
        componentB.saveReportToFile(report, REPORT_FILE);

        // Step 3: Verify that the file content matches the generated report
        boolean isVerified = componentB.verifyReportContent(REPORT_FILE, report);
        assertTrue(isVerified, "The file content does not match the generated report");
    }
}