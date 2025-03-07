// ComponentB.java
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class ComponentB {

    private JdbcTemplate jdbcTemplate;

    public ComponentB(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Retrieves a user from the database by email.
     *Java Test Runner
     * @param email The email of the user to retrieve.
     * @return A map containing the user's data.
     */
    public Map<String, Object> getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        return jdbcTemplate.queryForMap(sql, email);
    }
}