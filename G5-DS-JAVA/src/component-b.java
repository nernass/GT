// ComponentB.java
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class ComponentB {

    private JdbcTemplate jdbcTemplate;

    public ComponentB(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Retrieves a user from the database by email.
     *
     * @param email The email of the user to retrieve.
     * @return A list of maps containing the user's data (empty if the user does not exist).
     */
    public List<Map<String, Object>> getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        return jdbcTemplate.queryForList(sql, email);
    }
}