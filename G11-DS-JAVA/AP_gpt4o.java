
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class ComponentIntegrationTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ComponentA componentA;

    @InjectMocks
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);
    }

    @Test
    public void testSuccessPath() {
        int userId = 1;
        double initialBalance = 100.0;
        double paymentAmount = 50.0;
        double expectedBalance = 50.0;

        when(jdbcTemplate.queryForObject(anyString(), eq(Double.class), eq(userId))).thenReturn(initialBalance);
        doNothing().when(jdbcTemplate).update(anyString(), anyDouble(), anyInt());

        componentA.processPayment(userId, paymentAmount);
        double actualBalance = componentB.getUserBalance(userId);

        assertEquals(expectedBalance, actualBalance);
    }

    @Test
    public void testComponentAFailure() {
        int userId = 1;
        double initialBalance = 100.0;
        double paymentAmount = 150.0;

        when(jdbcTemplate.queryForObject(anyString(), eq(Double.class), eq(userId))).thenReturn(initialBalance);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            componentA.processPayment(userId, paymentAmount);
        });

        String expectedMessage = "Insufficient balance";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testEdgeCase() {
        int userId = 1;
        double initialBalance = 0.0;
        double paymentAmount = 0.0;
        double expectedBalance = 0.0;

        when(jdbcTemplate.queryForObject(anyString(), eq(Double.class), eq(userId))).thenReturn(initialBalance);
        doNothing().when(jdbcTemplate).update(anyString(), anyDouble(), anyInt());

        componentA.processPayment(userId, paymentAmount);
        double actualBalance = componentB.getUserBalance(userId);

        assertEquals(expectedBalance, actualBalance);
    }
}