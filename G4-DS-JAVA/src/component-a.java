// ComponentA.java
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

public class ComponentA {

    private JdbcTemplate jdbcTemplate;

    public ComponentA(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Updates a user's email in the database.
     *
     * @param userId The ID of the user to update.
     * @param newEmail The new email address.
     */
    public void updateUserEmail(int userId, String newEmail) {
        String sql = "UPDATE users SET email = ? WHERE id = ?";
        jdbcTemplate.update(sql, newEmail, userId);
    }
}