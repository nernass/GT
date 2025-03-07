import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

public class ComponentIntegrationTest {

    @Mock
    private JdbcTemplate mockJdbcTemplate;

    @Mock
    private DataSource mockDataSource;

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create components with mock datasource
        componentA = new ComponentA(mockDataSource);
        componentB = new ComponentB(mockDataSource);

        // Replace the JdbcTemplate in both components with our mock
        ReflectionTestUtils.setField(componentA, "jdbcTemplate", mockJdbcTemplate);
        ReflectionTestUtils.setField(componentB, "jdbcTemplate", mockJdbcTemplate);
    }

    @Test
    public void testSuccessfulPaymentProcessAndBalanceVerification() {
        // Success path - normal payment processing with verification
        int userId = 1;
        double initialBalance = 100.0;
        double paymentAmount = 30.0;
        double expectedBalance = 70.0;

        // Mock database responses
        when(mockJdbcTemplate.queryForObject(
                eq("SELECT balance FROM users WHERE id = ?"),
                eq(Double.class),
                eq(userId)))
                .thenReturn(initialBalance) // First call during payment processing
                .thenReturn(expectedBalance); // Second call during verification

        // Process payment through ComponentA
        componentA.processPayment(userId, paymentAmount);

        // Verify database update was called with correct parameters
        verify(mockJdbcTemplate).update(
                eq("UPDATE users SET balance = balance - ? WHERE id = ?"),
                eq(paymentAmount),
                eq(userId));

        // Verify the balance using ComponentB
        boolean verificationResult = componentB.verifyBalance(userId, expectedBalance);

        assertTrue(verificationResult);

        // Confirm ComponentB queried the database to check balance
        verify(mockJdbcTemplate, times(2)).queryForObject(
                eq("SELECT balance FROM users WHERE id = ?"),
                eq(Double.class),
                eq(userId));
    }

    @Test
    public void testInsufficientBalanceHandling() {
        // Failure path - insufficient balance
        int userId = 1;
        double initialBalance = 20.0;
        double paymentAmount = 50.0;

        when(mockJdbcTemplate.queryForObject(
                eq("SELECT balance FROM users WHERE id = ?"),
                eq(Double.class),
                eq(userId)))
                .thenReturn(initialBalance);

        // ComponentA should throw exception
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> componentA.processPayment(userId, paymentAmount));

        assertEquals("Insufficient balance", exception.getMessage());

        // Verify no database update was performed
        verify(mockJdbcTemplate, never()).update(anyString(), any(), any());

        // Verify balance remains unchanged using ComponentB
        when(mockJdbcTemplate.queryForObject(
                eq("SELECT balance FROM users WHERE id = ?"),
                eq(Double.class),
                eq(userId)))
                .thenReturn(initialBalance);

        boolean verificationResult = componentB.verifyBalance(userId, initialBalance);
        assertTrue(verificationResult);
    }

    @Test
    public void testExactBalancePayment() {
        // Edge case - payment amount equals available balance
        int userId = 1;
        double balance = 50.0;
        double paymentAmount = 50.0;
        double expectedBalance = 0.0;

        when(mockJdbcTemplate.queryForObject(
                eq("SELECT balance FROM users WHERE id = ?"),
                eq(Double.class),
                eq(userId)))
                .thenReturn(balance)
                .thenReturn(expectedBalance);

        componentA.processPayment(userId, paymentAmount);

        verify(mockJdbcTemplate).update(
                eq("UPDATE users SET balance = balance - ? WHERE id = ?"),
                eq(paymentAmount),
                eq(userId));

        boolean verificationResult = componentB.verifyBalance(userId, expectedBalance);
        assertTrue(verificationResult);
    }

    @Test
    public void testZeroAmountPayment() {
        // Edge case - zero amount payment
        int userId = 1;
        double initialBalance = 100.0;
        double paymentAmount = 0.0;

        when(mockJdbcTemplate.queryForObject(
                eq("SELECT balance FROM users WHERE id = ?"),
                eq(Double.class),
                eq(userId)))
                .thenReturn(initialBalance)
                .thenReturn(initialBalance); // Balance should remain unchanged

        componentA.processPayment(userId, paymentAmount);

        verify(mockJdbcTemplate).update(
                eq("UPDATE users SET balance = balance - ? WHERE id = ?"),
                eq(paymentAmount),
                eq(userId));

        boolean verificationResult = componentB.verifyBalance(userId, initialBalance);
        assertTrue(verificationResult);
    }

    @Test
    public void testNegativeAmountPayment() {
        // Edge case - negative amount (refund)
        int userId = 1;
        double initialBalance = 50.0;
        double paymentAmount = -20.0;
        double expectedBalance = 70.0;

        when(mockJdbcTemplate.queryForObject(
                eq("SELECT balance FROM users WHERE id = ?"),
                eq(Double.class),
                eq(userId)))
                .thenReturn(initialBalance)
                .thenReturn(expectedBalance);

        componentA.processPayment(userId, paymentAmount);

        verify(mockJdbcTemplate).update(
                eq("UPDATE users SET balance = balance - ? WHERE id = ?"),
                eq(paymentAmount),
                eq(userId));

        boolean verificationResult = componentB.verifyBalance(userId, expectedBalance);
        assertTrue(verificationResult);
    }
}