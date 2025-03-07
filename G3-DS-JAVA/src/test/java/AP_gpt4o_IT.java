import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
        // Mocking the database response for ComponentB
        when(jdbcTemplate.queryForMap(anyString(), anyString()))
                .thenReturn(Map.of("name", "John Doe", "email", "john.doe@example.com"));

        // Insert a user using ComponentA
        componentA.insertUser("John Doe", "john.doe@example.com");

        // Retrieve the user using ComponentB
        Map<String, Object> user = componentB.getUserByEmail("john.doe@example.com");

        // Assert the user data is correct
        assertEquals("John Doe", user.get("name"));
        assertEquals("john.doe@example.com", user.get("email"));
    }

    @Test
    public void testComponentBFailure() {
        // Mocking the database response for ComponentB to throw an exception
        when(jdbcTemplate.queryForMap(anyString(), anyString())).thenThrow(new RuntimeException("Database error"));

        // Insert a user using ComponentA
        componentA.insertUser("John Doe", "john.doe@example.com");

        // Try to retrieve the user using ComponentB and expect an exception
        assertThrows(RuntimeException.class, () -> {
            componentB.getUserByEmail("john.doe@example.com");
        });
    }

    @Test
    public void testInvalidInput() {
        // Mocking the database response for ComponentB
        when(jdbcTemplate.queryForMap(anyString(), anyString())).thenReturn(null);

        // Insert a user with invalid email using ComponentA
        componentA.insertUser("John Doe", "");

        // Try to retrieve the user using ComponentB and expect null
        Map<String, Object> user = componentB.getUserByEmail("");
        assertNull(user);
    }
}