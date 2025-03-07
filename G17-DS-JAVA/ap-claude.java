import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ComponentA and ComponentB interaction in the payment
 * process.
 * Tests the complete workflow from payment processing through balance update
 * and verification.
 */
public class PaymentIntegrationTest {
    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    @DisplayName("Complete payment workflow with successful transaction")
    void testCompletePaymentWorkflow() {
        // Setup test data
        String userId = "user123";
        double initialBalance = 100.0;
        double paymentAmount = 75.0;
        double expectedFinalBalance = 25.0;

        // Step 1: Process payment with ComponentA
        boolean paymentProcessed = componentA.processPayment(userId, paymentAmount, initialBalance);
        assertTrue(paymentProcessed, "Payment should be processed successfully");

        // Step 2: Update balance with ComponentB
        double updatedBalance = componentB.updateBalance(userId, paymentAmount, initialBalance);
        assertEquals(expectedFinalBalance, updatedBalance, "Balance should be reduced by payment amount");

        // Step 3: Verify transaction with ComponentB
        boolean transactionVerified = componentB.verifyTransaction(
                userId, paymentAmount, initialBalance, updatedBalance);
        assertTrue(transactionVerified, "Transaction should be verified successfully");
    }

    @Test
    @DisplayName("Payment workflow with invalid negative amount")
    void testPaymentWorkflowWithInvalidAmount() {
        // Setup test data
        String userId = "user123";
        double initialBalance = 100.0;
        double invalidAmount = -50.0;

        // Step 1: Try to process invalid payment with ComponentA
        boolean paymentProcessed = componentA.processPayment(userId, invalidAmount, initialBalance);
        assertFalse(paymentProcessed, "Payment with negative amount should be rejected");

        // Step 2: Verify ComponentB also rejects invalid amount
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            componentB.updateBalance(userId, invalidAmount, initialBalance);
        }, "ComponentB should reject negative amount with exception");

        assertTrue(exception.getMessage().contains("Invalid transaction"),
                "Exception should indicate invalid transaction");
    }

    @Test
    @DisplayName("Payment workflow with insufficient funds")
    void testPaymentWorkflowWithInsufficientFunds() {
        // Setup test data
        String userId = "user123";
        double initialBalance = 100.0;
        double excessiveAmount = 150.0;

        // Step 1: Try to process payment with insufficient funds using ComponentA
        boolean paymentProcessed = componentA.processPayment(userId, excessiveAmount, initialBalance);
        assertFalse(paymentProcessed, "Payment exceeding balance should be rejected");

        // Step 2: Verify ComponentB also rejects payment with insufficient funds
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            componentB.updateBalance(userId, excessiveAmount, initialBalance);
        }, "ComponentB should reject amount exceeding balance");

        assertTrue(exception.getMessage().contains("Invalid transaction"),
                "Exception should indicate invalid transaction");
    }

    @Test
    @DisplayName("Payment workflow with exact balance amount (edge case)")
    void testPaymentWorkflowWithExactBalance() {
        // Setup test data
        String userId = "user123";
        double initialBalance = 100.0;
        double paymentAmount = 100.0;
        double expectedFinalBalance = 0.0;

        // Step 1: Process payment with ComponentA
        boolean paymentProcessed = componentA.processPayment(userId, paymentAmount, initialBalance);
        assertTrue(paymentProcessed, "Payment using entire balance should be processed");

        // Step 2: Update balance with ComponentB
        double updatedBalance = componentB.updateBalance(userId, paymentAmount, initialBalance);
        assertEquals(expectedFinalBalance, updatedBalance, "Balance should be reduced to zero");

        // Step 3: Verify transaction with ComponentB
        boolean transactionVerified = componentB.verifyTransaction(
                userId, paymentAmount, initialBalance, updatedBalance);
        assertTrue(transactionVerified, "Transaction should be verified successfully");
    }

    @Test
    @DisplayName("Verify transaction fails when balance is incorrect")
    void testTransactionVerificationFailure() {
        // Setup test data
        String userId = "user123";
        double initialBalance = 100.0;
        double paymentAmount = 50.0;
        double incorrectNewBalance = 60.0; // Should be 50.0

        // Step 1: Process payment with ComponentA
        boolean paymentProcessed = componentA.processPayment(userId, paymentAmount, initialBalance);
        assertTrue(paymentProcessed, "Payment should be processed successfully");

        // Step 2: Simulate incorrect balance update (not using ComponentB properly)
        // This demonstrates what happens when components don't work together correctly

        // Step 3: Verify ComponentB detects incorrect balance
        boolean transactionVerified = componentB.verifyTransaction(
                userId, paymentAmount, initialBalance, incorrectNewBalance);

        assertFalse(transactionVerified, "Transaction verification should fail with incorrect balance");
}

@Test
    @DisplayName("Zero payment amount is rejected by both components")
    void testZeroPaymentAmount() {
        // Setup test data
        String userId = "user123";
        double initialBalance = 100.0;
        double zeroAmount = 0.0;
        
        // Step 1: Try to process payment with zero amount using ComponentA
        boolean paymentProcessed = componentA.processPaymentimport org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ComponentA and ComponentB interaction in the payment
 * process.
 * Tests the complete workflow from payment processing through balance update
 * and verification.
 */
public class PaymentIntegrationTest {
    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    @DisplayName("Complete payment workflow with successful transaction")
    void testCompletePaymentWorkflow() {
        // Setup test data
        String userId = "user123";
        double initialBalance = 100.0;
        double paymentAmount = 75.0;
        double expectedFinalBalance = 25.0;

        // Step 1: Process payment with ComponentA
        boolean paymentProcessed = componentA.processPayment(userId, paymentAmount, initialBalance);
        assertTrue(paymentProcessed, "Payment should be processed successfully");

        // Step 2: Update balance with ComponentB
        double updatedBalance = componentB.updateBalance(userId, paymentAmount, initialBalance);
        assertEquals(expectedFinalBalance, updatedBalance, "Balance should be reduced by payment amount");

        // Step 3: Verify transaction with ComponentB
        boolean transactionVerified = componentB.verifyTransaction(
                userId, paymentAmount, initialBalance, updatedBalance);
        assertTrue(transactionVerified, "Transaction should be verified successfully");
    }

    @Test
    @DisplayName("Payment workflow with invalid negative amount")
    void testPaymentWorkflowWithInvalidAmount() {
        // Setup test data
        String userId = "user123";
        double initialBalance = 100.0;
        double invalidAmount = -50.0;

        // Step 1: Try to process invalid payment with ComponentA
        boolean paymentProcessed = componentA.processPayment(userId, invalidAmount, initialBalance);
        assertFalse(paymentProcessed, "Payment with negative amount should be rejected");

        // Step 2: Verify ComponentB also rejects invalid amount
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            componentB.updateBalance(userId, invalidAmount, initialBalance);
        }, "ComponentB should reject negative amount with exception");

        assertTrue(exception.getMessage().contains("Invalid transaction"),
                "Exception should indicate invalid transaction");
    }

    @Test
    @DisplayName("Payment workflow with insufficient funds")
    void testPaymentWorkflowWithInsufficientFunds() {
        // Setup test data
        String userId = "user123";
        double initialBalance = 100.0;
        double excessiveAmount = 150.0;

        // Step 1: Try to process payment with insufficient funds using ComponentA
        boolean paymentProcessed = componentA.processPayment(userId, excessiveAmount, initialBalance);
        assertFalse(paymentProcessed, "Payment exceeding balance should be rejected");

        // Step 2: Verify ComponentB also rejects payment with insufficient funds
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            componentB.updateBalance(userId, excessiveAmount, initialBalance);
        }, "ComponentB should reject amount exceeding balance");

        assertTrue(exception.getMessage().contains("Invalid transaction"),
                "Exception should indicate invalid transaction");
    }

    @Test
    @DisplayName("Payment workflow with exact balance amount (edge case)")
    void testPaymentWorkflowWithExactBalance() {
        // Setup test data
        String userId = "user123";
        double initialBalance = 100.0;
        double paymentAmount = 100.0;
        double expectedFinalBalance = 0.0;

        // Step 1: Process payment with ComponentA
        boolean paymentProcessed = componentA.processPayment(userId, paymentAmount, initialBalance);
        assertTrue(paymentProcessed, "Payment using entire balance should be processed");

        // Step 2: Update balance with ComponentB
        double updatedBalance = componentB.updateBalance(userId, paymentAmount, initialBalance);
        assertEquals(expectedFinalBalance, updatedBalance, "Balance should be reduced to zero");

        // Step 3: Verify transaction with ComponentB
        boolean transactionVerified = componentB.verifyTransaction(
                userId, paymentAmount, initialBalance, updatedBalance);
        assertTrue(transactionVerified, "Transaction should be verified successfully");
    }

    @Test
    @DisplayName("Verify transaction fails when balance is incorrect")
    void testTransactionVerificationFailure() {
        // Setup test data
        String userId = "user123";
        double initialBalance = 100.0;
        double paymentAmount = 50.0;
        double incorrectNewBalance = 60.0; // Should be 50.0
        
        // Step 1: Process payment with ComponentA
        boolean paymentProcessed = componentA.processPayment(userId, paymentAmount, initialBalance);
        assertTrue(paymentProcessed, "Payment should be processed successfully");
        
        // Step 2: Simulate incorrect balance update (not using ComponentB properly)
        // This demonstrates what happens when components don't work together correctly
        
        // Step 3: Verify ComponentB detects incorrect balance
        boolean transactionVerified = componentB.verifyTransaction(
                userId, paymentAmount, initialBalance, incorrectNewBalance);
        
        assertFalse(transactionVerified, "Transaction verification should fail with incorrect balance");
    }

@Test
    @DisplayName("Zero payment amount is rejected by both components")
    void testZeroPaymentAmount() {
        // Setup test data
        String userId = "user123";
        double initialBalance = 100.0;
        double zeroAmount = 0.0;
        
        // Step 1: Try to process payment with zero amount using ComponentA
        boolean paymentProcessed = componentA.processPayment