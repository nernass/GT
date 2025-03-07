import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);
        componentA.jdbcTemplate = jdbcTemplate;
        componentB.jdbcTemplate = jdbcTemplate;
    }

    @Test
    public void testSuccessPath() {
        // Mocking the database response for ComponentB
        List<Map<String, Object>> mockUserData = Collections
                .singletonList(Collections.singletonMap("email", "test@example.com"));
        when(jdbcTemplate.queryForList(anyString(), anyString())).thenReturn(mockUserData);

        // Execute ComponentB to get user data
        List<Map<String, Object>> userData = componentB.getUserByEmail("test@example.com");
        assertNotNull(userData);
        assertFalse(userData.isEmpty());
        assertEquals("test@example.com", userData.get(0).get("email"));

        // Execute ComponentA to delete user data
        componentA.deleteUserByEmail("test@example.com");
        verify(jdbcTemplate, times(1)).update(anyString(), anyString());
    }

    @Test
    public void testPartialFailure() {
        // Mocking the database response for ComponentB to throw an exception
        when(jdbcTemplate.queryForList(anyString(), anyString())).thenThrow(new RuntimeException("Database error"));

        // Execute ComponentB to get user data and expect an exception
        assertThrows(RuntimeException.class, () -> componentB.getUserByEmail("test@example.com"));

        // Execute ComponentA to delete user data and expect no interaction due to
        // previous failure
        componentA.deleteUserByEmail("test@example.com");
        verify(jdbcTemplate, times(1)).update(anyString(), anyString());
    }

    @Test
    public void testEdgeCase() {
        // Mocking the database response for ComponentB with empty input
        when(jdbcTemplate.queryForList(anyString(), anyString())).thenReturn(Collections.emptyList());

        // Execute ComponentB to get user data with empty input
        List<Map<String, Object>> userData = componentB.getUserByEmail("");
        assertNotNull(userData);
        assertTrue(userData.isEmpty());

        // Execute ComponentA to delete user data with empty input
        componentA.deleteUserByEmail("");
        verify(jdbcTemplate, times(1)).update(anyString(), anyString());
    }
}