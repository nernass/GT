// ComponentA.java
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

public class ComponentA {

    private JdbcTemplate jdbcTemplate;

    public ComponentA(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Deletes a user from the database by email.
     *
     * @param email The email of the user to delete.
     */
    public void deleteUserByEmail(String email) {
        String sql = "DELETE FROM users WHERE email = ?";
        jdbcTemplate.update(sql, email);
    }
}