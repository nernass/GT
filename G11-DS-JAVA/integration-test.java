// IntegrationTest.java
import org.junit.jupiter.api.*;
import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    private DataSource dataSource;
    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        // Set up the database and components
        dataSource = DatabaseConfig.dataSource();
        DatabaseConfig.initializeDatabase(dataSource);
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);
    }

    @Test
    public void testIntegration() {
        // Test data
        int userId = 1; // John Doe
        double paymentAmount = 200.00;
        double expectedBalance = 800.00; // Initial balance: 1000.00

        // Step 1: Process a payment transaction using Component A
        componentA.processPayment(userId, paymentAmount);

        // Step 2: Verify the user's balance using Component B
        boolean isVerified = componentB.verifyBalance(userId, expectedBalance);
        assertTrue(isVerified, "The user's balance was not updated correctly");
    }

    @Test
    public void testInsufficientBalance() {
        // Test data
        int userId = 2; // Jane Doe
        double paymentAmount = 600.00; // Initial balance: 500.00

        // Step 1: Attempt to process a payment transaction with insufficient balance
        assertThrows(IllegalArgumentException.class, () -> {
            componentA.processPayment(userId, paymentAmount);
        }, "Expected an IllegalArgumentException for insufficient balance");
    }
}