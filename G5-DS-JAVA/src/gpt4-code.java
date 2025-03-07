import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
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
    public void testDeleteUserByEmailAndGetUserByEmail() {
        String email = "test@example.com";

        // Mocking the jdbcTemplate behavior
        when(jdbcTemplate.update(anyString(), anyString())).thenReturn(1);
        when(jdbcTemplate.queryForList(anyString(), anyString())).thenReturn(List.of(Map.of("email", email)));

        // Deleting the user
        componentA.deleteUserByEmail(email);
        verify(jdbcTemplate, times(1)).update("DELETE FROM users WHERE email = ?", email);

        // Retrieving the user
        List<Map<String, Object>> result = componentB.getUserByEmail(email);
        verify(jdbcTemplate, times(1)).queryForList("SELECT * FROM users WHERE email = ?", email);

        // Validating the result
        assertFalse(result.isEmpty());
        assertEquals(email, result.get(0).get("email"));
    }

    @Test
    public void testGetUserByEmailNotFound() {
        String email = "notfound@example.com";

        // Mocking the jdbcTemplate behavior
        when(jdbcTemplate.queryForList(anyString(), anyString())).thenReturn(List.of());

        // Retrieving the user
        List<Map<String, Object>> result = componentB.getUserByEmail(email);
        verify(jdbcTemplate, times(1)).queryForList("SELECT * FROM users WHERE email = ?", email);

        // Validating the result
        assertTrue(result.isEmpty());
    }
}