import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class ComponentABIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    @DisplayName("Integration test for successful payment flow")
    void successfulPaymentFlow() {
        // Setup
        String userId = "user123";
        double initialBalance = 1000.0;
        double paymentAmount = 200.0;

        // Process payment with ComponentA
        boolean paymentProcessed = componentA.processPayment(userId, paymentAmount, initialBalance);

        // Verify payment was processed
        assertTrue(paymentProcessed, "Payment should be processed successfully");

        // Update balance with ComponentB
        double updatedBalance = componentB.updateBalance(userId, paymentAmount, initialBalance);

        // Verify balance update
        assertEquals(initialBalance - paymentAmount, updatedBalance, 0.001,
                "Balance should be reduced by the payment amount");

        // Verify transaction with ComponentB
        boolean transactionVerified = componentB.verifyTransaction(
                userId, paymentAmount, initialBalance, updatedBalance);

        assertTrue(transactionVerified, "Transaction should be verified");
    }

    @Test
    @DisplayName("Integration test for insufficient balance")
    void insufficientBalanceFlow() {
        // Setup
        String userId = "user456";
        double initialBalance = 100.0;
        double paymentAmount = 200.0;

        // Process payment with ComponentA
        boolean paymentProcessed = componentA.processPayment(userId, paymentAmount, initialBalance);

        // Verify payment was not processed
        assertFalse(paymentProcessed, "Payment should be rejected due to insufficient balance");

        // Verify ComponentB throws exception when trying to update balance
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            componentB.updateBalance(userId, paymentAmount, initialBalance);
        });

        assertTrue(exception.getMessage().contains("Insufficient balance"),
                "Exception should mention insufficient balance");
    }

    @ParameterizedTest
    @CsvSource({
            "user789, 0.0, 500.0",
            "user789, -50.0, 500.0"
    })
    @DisplayName("Integration test for invalid payment amounts")
    void invalidPaymentAmountsFlow(String userId, double paymentAmount, double initialBalance) {
        // Process payment with ComponentA
        boolean paymentProcessed = componentA.processPayment(userId, paymentAmount, initialBalance);

        // Verify payment was not processed
        assertFalse(paymentProcessed, "Payment should be rejected due to invalid amount");

        // Verify ComponentB throws exception when trying to update balance
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            componentB.updateBalance(userId, paymentAmount, initialBalance);
        });

        assertTrue(exception.getMessage().contains("invalid amount"),
                "Exception should mention invalid amount");
    }

    @Test
    @DisplayName("Integration test for transaction verification failure")
    void transactionVerificationFailure() {
        // Setup
        String userId = "user101";
        double initialBalance = 500.0;
        double paymentAmount = 100.0;
        double incorrectNewBalance = 350.0; // Intentionally incorrect (should be 400.0)

        // Process payment with ComponentA
        boolean paymentProcessed = componentA.processPayment(userId, paymentAmount, initialBalance);

        // Verify payment was processed
        assertTrue(paymentProcessed, "Payment should be processed successfully");

        // Verify transaction with intentionally incorrect balance
        boolean transactionVerified = componentB.verifyTransaction(
                userId, paymentAmount, initialBalance, incorrectNewBalance);

        assertFalse(transactionVerified, "Transaction verification should fail due to incorrect balance");
    }
}