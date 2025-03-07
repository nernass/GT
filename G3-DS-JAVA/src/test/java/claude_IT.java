import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

class IntegrationTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(dataSource.getConnection()).thenReturn(null);
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);
    }

    @Test
    void testUserInsertionAndRetrieval() {
        // Test data
        String name = "John Doe";
        String email = "john.doe@example.com";

        // Mock user data
        Map<String, Object> expectedUser = new HashMap<>();
        expectedUser.put("name", name);
        expectedUser.put("email", email);

        // Mock JdbcTemplate behavior
        when(jdbcTemplate.queryForMap(anyString(), eq(email))).thenReturn(expectedUser);

        // Insert user
        componentA.insertUser(name, email);

        // Verify insertion
        verify(jdbcTemplate, times(1)).update(
                eq("INSERT INTO users (name, email) VALUES (?, ?)"),
                eq(name),
                eq(email));

        // Retrieve and verify user
        Map<String, Object> retrievedUser = componentB.getUserByEmail(email);
        assertNotNull(retrievedUser);
        assertEquals(name, retrievedUser.get("name"));
        assertEquals(email, retrievedUser.get("email"));
    }

    @Test
    void testUserNotFound() {
        String nonExistentEmail = "nonexistent@example.com";
        when(jdbcTemplate.queryForMap(anyString(), eq(nonExistentEmail)))
                .thenThrow(new org.springframework.dao.EmptyResultDataAccessException(1));

        assertThrows(org.springframework.dao.EmptyResultDataAccessException.class, () -> {
            componentB.getUserByEmail(nonExistentEmail);
        });
    }

    @Test
    void testDuplicateUserInsertion() {
        String name = "Jane Doe";
        String email = "jane.doe@example.com";

        when(jdbcTemplate.update(anyString(), any(), any()))
                .thenThrow(new org.springframework.dao.DuplicateKeyException("Duplicate email"));

        assertThrows(org.springframework.dao.DuplicateKeyException.class, () -> {
            componentA.insertUser(name, email);
        });
    }
}