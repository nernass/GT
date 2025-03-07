import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        DataSource dataSource = Mockito.mock(DataSource.class);
        jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);

        // Inject the mocked JdbcTemplate into the components
        componentA.jdbcTemplate = jdbcTemplate;
        componentB.jdbcTemplate = jdbcTemplate;
    }

    @Test
    public void testProcessPaymentAndVerifyBalance() {
        int userId = 1;
        double initialBalance = 100.0;
        double paymentAmount = 50.0;
        double expectedBalance = initialBalance - paymentAmount;

        // Mock the database interactions
        when(jdbcTemplate.queryForObject(anyString(), eq(Double.class), anyInt())).thenReturn(initialBalance,
                expectedBalance);
        doNothing().when(jdbcTemplate).update(anyString(), anyDouble(), anyInt());

        // Process the payment
        componentA.processPayment(userId, paymentAmount);

        // Verify the balance
        boolean isBalanceCorrect = componentB.verifyBalance(userId, expectedBalance);
        assertTrue(isBalanceCorrect);

        // Verify the interactions with the database
        verify(jdbcTemplate).queryForObject("SELECT balance FROM users WHERE id = ?", Double.class, userId);
        verify(jdbcTemplate).update("UPDATE users SET balance = balance - ? WHERE id = ?", paymentAmount, userId);
        verify(jdbcTemplate).queryForObject("SELECT balance FROM users WHERE id = ?", Double.class, userId);
    }

    @Test
    public void testProcessPaymentInsufficientBalance() {
        int userId = 1;
        double initialBalance = 30.0;
        double paymentAmount = 50.0;

        // Mock the database interactions
        when(jdbcTemplate.queryForObject(anyString(), eq(Double.class), anyInt())).thenReturn(initialBalance);

        // Attempt to process the payment and expect an exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            componentA.processPayment(userId, paymentAmount);
        });

        assertEquals("Insufficient balance", exception.getMessage());

        // Verify the interaction with the database
        verify(jdbcTemplate).queryForObject("SELECT balance FROM users WHERE id = ?", Double.class, userId);
    }
}