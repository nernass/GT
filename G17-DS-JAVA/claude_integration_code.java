import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ComponentIntegrationTest {
    private ComponentA componentA;
    private ComponentB componentB;
    private static final String TEST_USER_ID = "user123";
    private static final double INITIAL_BALANCE = 1000.0;

    @BeforeEach
    void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    void testSuccessfulPaymentFlow() {
        double paymentAmount = 500.0;

        // Process payment
        boolean paymentResult = componentA.processPayment(TEST_USER_ID, paymentAmount, INITIAL_BALANCE);
        assertTrue(paymentResult);

        // Update balance
        double newBalance = componentB.updateBalance(TEST_USER_ID, paymentAmount, INITIAL_BALANCE);
        assertEquals(INITIAL_BALANCE - paymentAmount, newBalance);

        // Verify transaction
        boolean verificationResult = componentB.verifyTransaction(TEST_USER_ID, paymentAmount,
                INITIAL_BALANCE, newBalance);
        assertTrue(verificationResult);
    }

    @Test
    void testInsufficientBalanceFlow() {
        double paymentAmount = 1500.0;

        // Attempt payment with insufficient balance
        boolean paymentResult = componentA.processPayment(TEST_USER_ID, paymentAmount, INITIAL_BALANCE);
        assertFalse(paymentResult);

        // Verify that balance update throws exception
        assertThrows(IllegalArgumentException.class,
                () -> componentB.updateBalance(TEST_USER_ID, paymentAmount, INITIAL_BALANCE));
    }

    @Test
    void testInvalidPaymentAmount() {
        double paymentAmount = -100.0;

        // Attempt payment with negative amount
        boolean paymentResult = componentA.processPayment(TEST_USER_ID, paymentAmount, INITIAL_BALANCE);
        assertFalse(paymentResult);

        // Verify that balance update throws exception
        assertThrows(IllegalArgumentException.class,
                () -> componentB.updateBalance(TEST_USER_ID, paymentAmount, INITIAL_BALANCE));
    }

    @Test
    void testTransactionVerification() {
        double paymentAmount = 300.0;
        double incorrectNewBalance = INITIAL_BALANCE - paymentAmount - 50.0; // Intentionally wrong

        boolean verificationResult = componentB.verifyTransaction(TEST_USER_ID, paymentAmount,
                INITIAL_BALANCE, incorrectNewBalance);
        assertFalse(verificationResult);
    }
}