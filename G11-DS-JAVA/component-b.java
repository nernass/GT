// ComponentB.java
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

public class ComponentB {

    private JdbcTemplate jdbcTemplate;

    public ComponentB(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Fetches the user's balance from the database.
     *
     * @param userId The ID of the user.
     * @return The user's balance.
     */
    public double getUserBalance(int userId) {
        String sql = "SELECT balance FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Double.class, userId);
    }

    /**
     * Verifies that the user's balance was updated correctly after the payment transaction.
     *
     * @param userId The ID of the user.
     * @param expectedBalance The expected balance after the payment transaction.
     * @return True if the balance matches the expected balance, false otherwise.
     */
    public boolean verifyBalance(int userId, double expectedBalance) {
        double actualBalance = getUserBalance(userId);
        return actualBalance == expectedBalance;
    }
}