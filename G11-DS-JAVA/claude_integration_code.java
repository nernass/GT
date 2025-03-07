import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ComponentIntegrationTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(dataSource.getConnection()).thenReturn(null);
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);
    }

    @Test
    void testSuccessfulPaymentTransaction() {
        // Arrange
        int userId = 1;
        double initialBalance = 1000.0;
        double paymentAmount = 500.0;
        double expectedBalance = 500.0;

        when(jdbcTemplate.queryForObject(
                eq("SELECT balance FROM users WHERE id = ?"),
                eq(Double.class),
                eq(userId))).thenReturn(initialBalance, expectedBalance);

        // Act
        componentA.processPayment(userId, paymentAmount);
        boolean verificationResult = componentB.verifyBalance(userId, expectedBalance);

        // Assert
        assertTrue(verificationResult);
        verify(jdbcTemplate, times(1)).update(
                eq("UPDATE users SET balance = balance - ? WHERE id = ?"),
                eq(paymentAmount),
                eq(userId));
    }

    @Test
    void testInsufficientBalancePayment() {
        // Arrange
        int userId = 1;
        double initialBalance = 100.0;
        double paymentAmount = 500.0;

        when(jdbcTemplate.queryForObject(
                eq("SELECT balance FROM users WHERE id = ?"),
                eq(Double.class),
                eq(userId))).thenReturn(initialBalance);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            componentA.processPayment(userId, paymentAmount);
        });

        verify(jdbcTemplate, never()).update(
                eq("UPDATE users SET balance = balance - ? WHERE id = ?"),
                any(Double.class),
                any(Integer.class));
    }

    @Test
    void testVerifyBalanceAfterFailedTransaction() {
        // Arrange
        int userId = 1;
        double initialBalance = 1000.0;

        when(jdbcTemplate.queryForObject(
                eq("SELECT balance FROM users WHERE id = ?"),
                eq(Double.class),
                eq(userId))).thenReturn(initialBalance);

        // Act
        boolean verificationResult = componentB.verifyBalance(userId, initialBalance);

        // Assert
        assertTrue(verificationResult);
    }
}