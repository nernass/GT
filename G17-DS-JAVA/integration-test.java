import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    @Test
    public void testPaymentProcessingAndBalanceUpdate() {
        // Create instances of ComponentA and ComponentB
        ComponentA paymentProcessor = new ComponentA();
        ComponentB balanceUpdater = new ComponentB();

        // Test data
        String userId = "user123";
        double amount = 50.0;
        double initialBalance = 100.0;

        // Process the payment using ComponentA
        boolean isPaymentSuccessful = paymentProcessor.processPayment(userId, amount, initialBalance);
        assertTrue(isPaymentSuccessful, "Payment processing failed");

        // Update the user's balance using ComponentB
        double updatedBalance = balanceUpdater.updateBalance(userId, amount, initialBalance);
        assertEquals(initialBalance - amount, updatedBalance, "Balance update failed");

        // Verify the transaction using ComponentB
        boolean isTransactionVerified = balanceUpdater.verifyTransaction(userId, amount, initialBalance, updatedBalance);
        assertTrue(isTransactionVerified, "Transaction verification failed");
    }
}