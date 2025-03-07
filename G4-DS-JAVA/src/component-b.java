// ComponentB.java
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.util.Map;

public class ComponentB {

    private JdbcTemplate jdbcTemplate;

    public ComponentB(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Retrieves a user from the database by ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return A map containing the user's data.
     */
    public Map<String, Object> getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForMap(sql, userId);
    }
}