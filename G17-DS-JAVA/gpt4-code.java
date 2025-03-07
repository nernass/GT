import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    public void testSuccessfulPaymentAndBalanceUpdate() {
        String userId = "user123";
        double initialBalance = 100.0;
        double paymentAmount = 50.0;

        // Process payment
        boolean paymentSuccess = componentA.processPayment(userId, paymentAmount, initialBalance);
        assertTrue(paymentSuccess, "Payment should be successful");

        // Update balance
        double updatedBalance = componentB.updateBalance(userId, paymentAmount, initialBalance);
        assertEquals(50.0, updatedBalance, 0.01, "Balance should be updated correctly");

        // Verify transaction
        boolean transactionVerified = componentB.verifyTransaction(userId, paymentAmount, initialBalance, updatedBalance);
        assertTrue(transactionVerified, "Transaction should be verified successfully");
    }

    @Test
    public void testInsufficientBalance() {
        String userId = "user123";
        double initialBalance = 30.0;
        double paymentAmount = 50.0;

        // Process payment
        boolean paymentSuccess = componentA.processPayment(userId, paymentAmount, initialBalance);
        assertFalse(paymentSuccess, "Payment should fail due to insufficient balance");

        // Attempt to update balance and expect an exception
        assertThrows(IllegalArgumentException.class, () -> {
            componentB.updateBalance(userId, paymentAmount, initialBalance);
        }, "Should throw IllegalArgumentException due to insufficient balance");
    }

    @Test
    public void testInvalidPaymentAmount() {
        String userId = "user123";
        double initialBalance = 100.0;
        double paymentAmount = -10.0;

        // Process payment
        boolean paymentSuccess = componentA.processPayment(userId, paymentAmount, initialBalance);
        assertFalse(paymentSuccess, "Payment should fail due to invalid amount");

        // Attempt to update balance and expect an exception
        assertThrows(IllegalArgumentException.class, () -> {
            componentB.updateBalance(userId, paymentAmount, initialBalance);
        }, "Should throw IllegalArgumentException due to invalid amount");
    }
}