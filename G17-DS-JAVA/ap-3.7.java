import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    @DisplayName("Test successful payment flow through ComponentA and ComponentB")
    void testSuccessfulPaymentFlow() {
        // Setup test data
        String userId = "user123";
        double amount = 50.0;
        double initialBalance = 100.0;

        // Step 1: Process payment through ComponentA
        boolean paymentProcessed = componentA.processPayment(userId, amount, initialBalance);

        // Assert payment was processed successfully
        assertTrue(paymentProcessed, "Payment should be processed successfully");

        // Step 2: Update balance through ComponentB
        double updatedBalance = componentB.updateBalance(userId, amount, initialBalance);

        // Assert balance was updated correctly
        assertEquals(50.0, updatedBalance, 0.001, "Balance should be updated correctly");

        // Step 3: Verify transaction through ComponentB
        boolean transactionVerified = componentB.verifyTransaction(userId, amount, initialBalance, updatedBalance);

        // Assert transaction was verified successfully
        assertTrue(transactionVerified, "Transaction should be verified successfully");
    }

    @Test
    @DisplayName("Test payment flow with insufficient balance")
    void testInsufficientBalanceFlow() {
        // Setup test data
        String userId = "user456";
        double amount = 150.0;
        double initialBalance = 100.0;

        // Step 1: Process payment through ComponentA
        boolean paymentProcessed = componentA.processPayment(userId, amount, initialBalance);

        // Assert payment was not processed due to insufficient balance
        assertFalse(paymentProcessed, "Payment should fail due to insufficient balance");

        // Step 2: Since payment failed, balance should not be updated
        // We verify this by checking that an exception is thrown when trying to update
        // balance
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            componentB.updateBalance(userId, amount, initialBalance);
        });

        assertTrue(exception.getMessage().contains("Insufficient balance"),
                "Exception should mention insufficient balance");
    }

    @Test
    @DisplayName("Test payment flow with invalid amount")
    void testInvalidAmountFlow() {
        // Setup test data
        String userId = "user789";
        double amount = -50.0;
        double initialBalance = 100.0;

        // Step 1: Process payment through ComponentA
        boolean paymentProcessed = componentA.processPayment(userId, amount, initialBalance);

        // Assert payment was not processed due to invalid amount
        assertFalse(paymentProcessed, "Payment should fail due to invalid amount");

        // Step 2: Since payment failed, balance should not be updated
        // We verify this by checking that an exception is thrown when trying to update
        // balance
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            componentB.updateBalance(userId, amount, initialBalance);
        });

        assertTrue(exception.getMessage().contains("invalid amount"),
                "Exception should mention invalid amount");
    }

    @Test
    @DisplayName("Test transaction verification with incorrect balance calculation")
    void testIncorrectBalanceCalculation() {
        // Setup test data
        String userId = "user101";
        double amount = 30.0;
        double initialBalance = 100.0;
        double incorrectUpdatedBalance = 65.0; // Incorrect: should be 70.0

        // Step 1: Process payment through ComponentA
        boolean paymentProcessed = componentA.processPayment(userId, amount, initialBalance);

        // Assert payment was processed successfully
        assertTrue(paymentProcessed, "Payment should be processed successfully");

        // Step 2: Verify transaction with incorrect updated balance
        boolean transactionVerified = componentB.verifyTransaction(
                userId, amount, initialBalance, incorrectUpdatedBalance);

        // Assert transaction verification failed due to incorrect balance
        assertFalse(transactionVerified, "Transaction verification should fail due to incorrect balance");
    }

    @Test
    @DisplayName("Test end-to-end payment flow with zero balance after payment")
    void testZeroBalanceAfterPayment() {
        // Setup test data
        String userId = "user202";
        double amount = 100.0;
        double initialBalance = 100.0;

        // Step 1: Process payment through ComponentA
        boolean paymentProcessed = componentA.processPayment(userId, amount, initialBalance);

        // Assert payment was processed successfully
        assertTrue(paymentProcessed, "Payment should be processed successfully");

        // Step 2: Update balance through ComponentB
        double updatedBalance = componentB.updateBalance(userId, amount, initialBalance);

        // Assert balance was updated to zero
        assertEquals(0.0, updatedBalance, 0.001, "Balance should be zero after payment");

        // Step 3: Verify transaction through ComponentB
        boolean transactionVerified = componentB.verifyTransaction(userId, amount, initialBalance, updatedBalance);

        // Assert transaction was verified successfully
        assertTrue(transactionVerified, "Transaction should be verified successfully");
    }
}