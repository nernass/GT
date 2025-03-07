
// ComponentIntegrationTest.java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    @BeforeEach
    public void setUp() {
        dataSource = mock(DataSource.class);
        jdbcTemplate = mock(JdbcTemplate.class);
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);
        componentA.jdbcTemplate = jdbcTemplate;
        componentB.jdbcTemplate = jdbcTemplate;
    }

    @Test
    public void testUpdateUserEmailAndRetrieveUser() {
        int userId = 1;
        String newEmail = "newemail@example.com";
        Map<String, Object> userData = Map.of("id", userId, "email", newEmail);

        doNothing().when(jdbcTemplate).update(anyString(), anyString(), anyInt());
        when(jdbcTemplate.queryForMap(anyString(), anyInt())).thenReturn(userData);

        componentA.updateUserEmail(userId, newEmail);
        Map<String, Object> result = componentB.getUserById(userId);

        verify(jdbcTemplate, times(1)).update(anyString(), eq(newEmail), eq(userId));
        verify(jdbcTemplate, times(1)).queryForMap(anyString(), eq(userId));
        assertEquals(newEmail, result.get("email"));
    }

    @Test
    public void testGetUserByIdNotFound() {
        int userId = 2;

        when(jdbcTemplate.queryForMap(anyString(), anyInt())).thenThrow(new RuntimeException("User not found"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            componentB.getUserById(userId);
        });

        assertEquals("User not found", exception.getMessage());
        verify(jdbcTemplate, times(1)).queryForMap(anyString(), eq(userId));
    }
}