java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
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
    public void setUp() {
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);
        
        // Replace the JdbcTemplate in both components with our mock
        ReflectionTestUtils.setField(componentA, "jdbcTemplate", jdbcTemplate);
        ReflectionTestUtils.setField(componentB, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    public void testDeleteExistingUser() {
        // Arrange
        String email = "test@example.com";
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1);
        user.put("email", email);
        user.put("name", "Test User");
        List<Map<String, Object>> users = Collections.singletonList(user);
        
        // Mock behavior
        when(jdbcTemplate.queryForList(anyString(), eq(email))).thenReturn(users);
        
        // Act - First verify user exists
        List<Map<String, Object>> result = componentB.getUserByEmail(email);
        
        // Assert user exists
        assertFalse(result.isEmpty());
        assertEquals(email, result.get(0).get("email"));
        
        // Act - Delete the user
        componentA.deleteUserByEmail(email);
        
        // Assert deletion was called
        verify(jdbcTemplate).update(anyString(), eq(email));
        
        // Mock behavior after deletion
        when(jdbcTemplate.queryForList(anyString(), eq(email))).thenReturn(Collections.emptyList());
        
        // Act - Verify user no longer exists
        List<Map<String, Object>> afterDeleteResult = componentB.getUserByEmail(email);
        
        // Assert user no longer exists
        assertTrue(afterDeleteResult.isEmpty());
    }

    @Test
    public void testDeleteNonExistingUser() {
        // Arrange
        String email = "nonexistent@example.com";
        
        // Mock behavior - User doesn't exist
        when(jdbcTemplate.queryForList(anyString(), eq(email))).thenReturn(Collections.emptyList());
        
        // Act - Verify user doesn't exist initially
        List<Map<String, Object>> result = componentB.getUserByEmail(email);
        
        // Assert user doesn't exist
        assertTrue(result.isEmpty());
        
        // Act - Try to delete non-existent user
        componentA.deleteUserByEmail(email);
        
        // Assert deletion was attempted but nothing happened
        verify(jdbcTemplate).update(anyString(), eq(email));
        
        // Act - Verify user still doesn't exist
        List<Map<String, Object>> afterDeleteResult = componentB.getUserByEmail(email);
        
        // Assert user still doesn't exist
        assertTrue(afterDeleteResult.isEmpty());
    }

    @Test
    public void testDatabaseErrorHandling() {
        // Arrange
        String email = "test@example.com";
        RuntimeException dbException = new RuntimeException("Database connection error");
        
        // Mock behavior - database throws exception
        when(jdbcTemplate.queryForList(anyString(), eq(email))).thenThrow(dbException);
        
        // Act & Assert - Verify exception is propagated
        Exception exception = assertThrows(RuntimeException.class, () -> {
            componentB.getUserByEmail(email);
        });
        assertEquals("Database connection error", exception.getMessage());
        
        // Mock behavior - database throws exception on delete too
        doThrow(dbException).when(jdbcTemplate).update(anyString(), eq(email));
        
        // Act & Assert - Verify exception is propagated from delete operation
        Exception deleteException = assertThrows(RuntimeException.class, () -> {
            componentA.deleteUserByEmail(email);
        });
        assertEquals("Database connection error", deleteException.getMessage());
    }
}