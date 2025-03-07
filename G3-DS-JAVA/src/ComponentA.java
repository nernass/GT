// ComponentA.java
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

public class ComponentA {

    private JdbcTemplate jdbcTemplate;

    public ComponentA(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Inserts a new user into the database.
     *
     * @param name  The name of the user.
     * @param email The email of the user.
     */
    public void insertUser(String name, String email) {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        jdbcTemplate.update(sql, name, email);
    }
}