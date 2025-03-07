// ComponentA.java
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

public class ComponentA {

    private JdbcTemplate jdbcTemplate;

    public ComponentA(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Processes a payment transaction by deducting the payment amount from the user's balance.
     *
     * @param userId The ID of the user.
     * @param amount The payment amount.
     * @throws IllegalArgumentException If the user does not have sufficient balance.
     */
    public void processPayment(int userId, double amount) {
        // Check if the user has sufficient balance
        String checkBalanceSql = "SELECT balance FROM users WHERE id = ?";
        double balance = jdbcTemplate.queryForObject(checkBalanceSql, Double.class, userId);

        if (balance < amount) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        // Deduct the payment amount from the user's balance
        String updateBalanceSql = "UPDATE users SET balance = balance - ? WHERE id = ?";
        jdbcTemplate.update(updateBalanceSql, amount, userId);
    }
}