import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    private EmbeddedDatabase dataSource;

    @BeforeEach
    public void setup() {
        // Create an in-memory database for testing
        dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql") // You would create this to define your users table
                .build();

        // Initialize the database with test data
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("INSERT INTO users (id, balance) VALUES (?, ?)", 1, 1000.0);
        jdbcTemplate.update("INSERT INTO users (id, balance) VALUES (?, ?)", 2, 50.0);
        jdbcTemplate.update("INSERT INTO users (id, balance) VALUES (?, ?)", 3, 0.0);

        // Create real component instances with the test database
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);
    }

    @Test
    public void testSuccessfulPaymentProcessing() {
        // Test data
        int userId = 1;
        double paymentAmount = 200.0;
        double initialBalance = 1000.0;
        double expectedBalance = 800.0;

        // Process the payment using ComponentA
        componentA.processPayment(userId, paymentAmount);

        // Verify the balance was updated correctly using ComponentB
        boolean balanceVerified = componentB.verifyBalance(userId, expectedBalance);

        assertTrue(balanceVerified, "Balance should be updated correctly after payment");
        assertEquals(expectedBalance, componentB.getUserBalance(userId), 0.001,
                "The user balance should match the expected value");
    }

    @Test
    public void testInsufficientBalanceFailure() {
        // Test data
        int userId = 2;
        double paymentAmount = 100.0; // More than the user's balance of 50.0
        double initialBalance = 50.0;

        // The payment should fail due to insufficient balance
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            componentA.processPayment(userId, paymentAmount);
        });

        assertEquals("Insufficient balance", exception.getMessage());

        // Verify the balance remains unchanged using ComponentB
        assertTrue(componentB.verifyBalance(userId, initialBalance),
                "Balance should remain unchanged after failed payment attempt");
    }

    @Test
    public void testZeroBalanceEdgeCase() {
        // Test data
        int userId = 3;
        double paymentAmount = 0.0;
        double initialBalance = 0.0;

        // Process a zero-amount payment
        componentA.processPayment(userId, paymentAmount);

        // Verify the balance remains unchanged
        assertTrue(componentB.verifyBalance(userId, initialBalance),
                "Balance should remain zero after zero-amount payment");
    }

    @Test
    public void testNegativeAmountEdgeCase() {
        // Test data
        int userId = 1;
        double paymentAmount = -100.0; // Negative amount should be rejected
        double initialBalance = 1000.0;

        // The payment with negative amount should be rejected
        // Note: This assumes ComponentA would validate and reject negative amounts
        // If ComponentA doesn't have this validation, this test would need to be
        // adjusted
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            componentA.processPayment(userId, paymentAmount);
        });

        // Verify balance remains unchanged
        assertTrue(componentB.verifyBalance(userId, initialBalance),
                "Balance should remain unchanged after invalid payment attempt");
    }

    @Test
    public void testExactBalanceEdgeCase() {
        // Test data
        int userId = 2;
        double paymentAmount = 50.0; // Exactly the user's balance
        double expectedBalance = 0.0;

        // Process a payment that uses the entire balance
        componentA.processPayment(userId, paymentAmount);

        // Verify the balance is now zero
        assertTrue(componentB.verifyBalance(userId, expectedBalance),
                "Balance should be zero after using entire balance for payment");
    }
}