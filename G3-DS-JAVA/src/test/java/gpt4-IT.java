import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
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
        dataSource = Mockito.mock(DataSource.class);
        jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);
        componentA.jdbcTemplate = jdbcTemplate;
        componentB.jdbcTemplate = jdbcTemplate;
    }

    @Test
    public void testInsertAndRetrieveUser() {
        String name = "John Doe";
        String email = "john.doe@example.com";

        // Mock the insert operation
        doNothing().when(jdbcTemplate).update(anyString(), anyString(), anyString());

        // Mock the retrieve operation
        Map<String, Object> mockUser = Map.of("name", name, "email", email);
        when(jdbcTemplate.queryForMap(anyString(), anyString())).thenReturn(mockUser);

        // Insert user
        componentA.insertUser(name, email);

        // Retrieve user
        Map<String, Object> retrievedUser = componentB.getUserByEmail(email);

        // Validate the retrieved user
        assertNotNull(retrievedUser);
        assertEquals(name, retrievedUser.get("name"));
        assertEquals(email, retrievedUser.get("email"));

        // Verify interactions
        verify(jdbcTemplate, times(1)).update(anyString(), eq(name), eq(email));
        verify(jdbcTemplate, times(1)).queryForMap(anyString(), eq(email));
    }
}