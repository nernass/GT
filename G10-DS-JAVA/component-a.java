// ComponentA.java
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class ComponentA {

    private JdbcTemplate jdbcTemplate;

    public ComponentA(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Generates a report based on database data.
     *
     * @return A report as a String.
     */
    public String generateReport() {
        // Query the database to get sales data
        String sql = "SELECT product, quantity, price FROM sales";
        List<Map<String, Object>> salesData = jdbcTemplate.queryForList(sql);

        // Generate the report
        StringBuilder report = new StringBuilder();
        report.append("Sales Report:\n");
        report.append("---------------------------------\n");
        for (Map<String, Object> row : salesData) {
            report.append("Product: ").append(row.get("product")).append(", ");
            report.append("Quantity: ").append(row.get("quantity")).append(", ");
            report.append("Price: ").append(row.get("price")).append("\n");
        }
        report.append("---------------------------------\n");
        return report.toString();
    }
}