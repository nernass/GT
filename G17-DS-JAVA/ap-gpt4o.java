import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ComponentIntegrationTest {

    @Mock
    private ComponentB componentB;

    @InjectMocks
    private ComponentA componentA;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSuccessPath() {
        String userId = "user123";
        double amount = 50.0;
        double userBalance = 100.0;
        double updatedBalance = 50.0;

        when(componentB.updateBalance(userId, amount, userBalance)).thenReturn(updatedBalance);
        when(componentB.verifyTransaction(userId, amount, userBalance, updatedBalance)).thenReturn(true);

        boolean paymentResult = componentA.processPayment(userId, amount, userBalance);
        assertTrue(paymentResult);
        verify(componentB).updateBalance(userId, amount, userBalance);
        verify(componentB).verifyTransaction(userId, amount, userBalance, updatedBalance);
    }

    @Test
    public void testComponentBFailure() {
        String userId = "user123";
        double amount = 50.0;
        double userBalance = 100.0;

        when(componentB.updateBalance(userId, amount, userBalance))
                .thenThrow(new IllegalArgumentException("Invalid transaction"));

        boolean paymentResult = componentA.processPayment(userId, amount, userBalance);
        assertFalse(paymentResult);
        verify(componentB).updateBalance(userId, amount, userBalance);
    }

    @Test
    public void testInvalidInputToComponentA() {
        String userId = "user123";
        double amount = -10.0;
        double userBalance = 100.0;

        boolean paymentResult = componentA.processPayment(userId, amount, userBalance);
        assertFalse(paymentResult);
        verify(componentB, never()).updateBalance(anyString(), anyDouble(), anyDouble());
    }
}