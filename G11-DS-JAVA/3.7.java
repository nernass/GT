import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComponentIntegrationTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    void setUp() {
        // Create components with mock dataSource
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);

        // Replace jdbcTemplate with mock in both components using reflection
        try {
            java.lang.reflect.Field fieldA = ComponentA.class.getDeclaredField("jdbcTemplate");
            fieldA.setAccessible(true);
            fieldA.set(componentA, jdbcTemplate);

            java.lang.reflect.Field fieldB = ComponentB.class.getDeclaredField("jdbcTemplate");
            fieldB.setAccessible(true);
            fieldB.set(componentB, jdbcTemplate);
        } catch (Exception e) {
            fail("Failed to set up mocks: " + e.getMessage());
        }
    }

    @Test
    void testSuccessfulPaymentProcessing() {
        // Setup
        int userId = 1;
        double initialBalance = 100.0;
        double paymentAmount = 50.0;
        double expectedBalance = 50.0;

        // Mock the database queries
        when(jdbcTemplate.queryForObject(anyString(), eq(Double.class), eq(userId)))
                .thenReturn(initialBalance) // First call in processPayment
                .thenReturn(expectedBalance); // Second call in verifyBalance

        // Process payment
        componentA.processPayment(userId, paymentAmount);

        // Verify database update was called correctly
        verify(jdbcTemplate).update(anyString(), eq(paymentAmount), eq(userId));

        // Verify balance after payment
        boolean result = componentB.verifyBalance(userId, expectedBalance);

        // Assert
        assertTrue(result, "Balance verification should succeed");
    }

    @Test
    void testInsufficientBalancePaymentProcessing() {
        // Setup
        int userId = 1;
        double initialBalance = 30.0;
        double paymentAmount = 50.0;

        // Mock the database query for balance check
        when(jdbcTemplate.queryForObject(anyString(), eq(Double.class), eq(userId)))
                .thenReturn(initialBalance);

        // Process payment should throw exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            componentA.processPayment(userId, paymentAmount);
        });

        // Assert
        assertEquals("Insufficient balance", exception.getMessage());

        // Verify database update was not called
        verify(jdbcTemplate, never()).update(anyString(), anyDouble(), anyInt());
    }

    @Test
    void testBalanceVerificationFailure() {
        // Setup
        int userId = 1;
        double actualBalance = 75.0;
        double expectedBalance = 50.0;

        // Mock the database query
        when(jdbcTemplate.queryForObject(anyString(), eq(Double.class), eq(userId)))
                .thenReturn(actualBalance);

        // Verify balance
        boolean result = componentB.verifyBalance(userId, expectedBalance);

        // Assert
        assertFalse(result, "Balance verification should fail when actual and expected balances differ");
    }
}