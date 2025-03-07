import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ComponentIntegrationTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock JdbcTemplate behavior using the same DataSource
        when(dataSource.getConnection()).thenReturn(null); // Not actually used in our test
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);

        // Use reflection to replace jdbcTemplate in both components with our mock
        try {
            java.lang.reflect.Field fieldA = ComponentA.class.getDeclaredField("jdbcTemplate");
            fieldA.setAccessible(true);
            fieldA.set(componentA, jdbcTemplate);

            java.lang.reflect.Field fieldB = ComponentB.class.getDeclaredField("jdbcTemplate");
            fieldB.setAccessible(true);
            fieldB.set(componentB, jdbcTemplate);
        } catch (Exception e) {
            fail("Failed to set up mock JdbcTemplate: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateAndRetrieveUserEmail() {
        // Prepare test data
        int userId = 1;
        String newEmail = "updated@example.com";
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", userId);
        userData.put("email", newEmail);
        userData.put("name", "Test User");

        // Configure mock behavior
        doNothing().when(jdbcTemplate).update(anyString(), anyString(), anyInt());
        when(jdbcTemplate.queryForMap(anyString(), anyInt())).thenReturn(userData);

        // Execute the component methods
        componentA.updateUserEmail(userId, newEmail);
        Map<String, Object> result = componentB.getUserById(userId);

        // Verify interactions
        verify(jdbcTemplate).update("UPDATE users SET email = ? WHERE id = ?", newEmail, userId);
        verify(jdbcTemplate).queryForMap("SELECT * FROM users WHERE id = ?", userId);

        // Assert results
        assertNotNull(result);
        assertEquals(userId, result.get("id"));
        assertEquals(newEmail, result.get("email"));
    }

    @Test
    public void testUserNotFound() {
        // Prepare test data
        int nonExistentUserId = 999;

        // Configure mock behavior for user not found
        when(jdbcTemplate.queryForMap(anyString(), eq(nonExistentUserId)))
                .thenThrow(new EmptyResultDataAccessException(1));

        // Verify exception is thrown when trying to get non-existent user
        assertThrows(EmptyResultDataAccessException.class, () -> {
            componentB.getUserById(nonExistentUserId);
        });
    }

    @Test
    public void testUpdateNonExistentUser() {
        // Prepare test data
        int nonExistentUserId = 999;
        String newEmail = "nonexistent@example.com";

        // Configure mock to simulate no rows affected
        doReturn(0).when(jdbcTemplate).update(anyString(), anyString(), eq(nonExistentUserId));

        // Execute the update
        componentA.updateUserEmail(nonExistentUserId, newEmail);

        // Verify the update was attempted
        verify(jdbcTemplate).update("UPDATE users SET email = ? WHERE id = ?", newEmail, nonExistentUserId);
    }
}