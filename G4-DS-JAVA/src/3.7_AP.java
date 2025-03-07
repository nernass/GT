import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setup() {
        // Set up an embedded database for testing
        EmbeddedDatabase db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql") // Create tables
                .addScript("test-data.sql") // Insert test data
                .build();

        dataSource = db;
        jdbcTemplate = new JdbcTemplate(dataSource);

        // Create real components
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);
    }

    @Test
    public void testUpdateUserEmailAndRetrieveUser() {
        // Setup - assume user with ID 1 exists in test-data.sql with email
        // "old@example.com"
        int userId = 1;
        String newEmail = "new@example.com";

        // Execute update through ComponentA
        componentA.updateUserEmail(userId, newEmail);

        // Verify through ComponentB that the email was updated
        Map<String, Object> user = componentB.getUserById(userId);

        // Assert
        assertNotNull(user);
        assertEquals(userId, user.get("id"));
        assertEquals(newEmail, user.get("email"));
    }

    @Test
    public void testUpdateWithNonExistentUser() {
        // Setup - using an ID that doesn't exist in our test data
        int nonExistentUserId = 999;
        String newEmail = "nonexistent@example.com";

        // Execute update through ComponentA - should not throw exception, just not
        // update anything
        componentA.updateUserEmail(nonExistentUserId, newEmail);

        // Try to retrieve with ComponentB - should throw exception since user doesn't
        // exist
        Exception exception = assertThrows(Exception.class, () -> {
            componentB.getUserById(nonExistentUserId);
        });

        // Verify exception is related to no data found
        assertTrue(exception.getMessage().contains("Incorrect result size") ||
                exception.getMessage().contains("no data"));
    }

    @Test
    public void testWithMockedComponents() {
        // Setup with mocks to isolate the interaction without database
        DataSource mockDataSource = mock(DataSource.class);
        JdbcTemplate mockJdbcTemplate = mock(JdbcTemplate.class);

        ComponentA componentA = new ComponentA(mockDataSource);
        ComponentB componentB = new ComponentB(mockDataSource);

        // Inject mocked JdbcTemplate using reflection
        try {
            java.lang.reflect.Field jdbcField1 = ComponentA.class.getDeclaredField("jdbcTemplate");
            jdbcField1.setAccessible(true);
            jdbcField1.set(componentA, mockJdbcTemplate);

            java.lang.reflect.Field jdbcField2 = ComponentB.class.getDeclaredField("jdbcTemplate");
            jdbcField2.setAccessible(true);
            jdbcField2.set(componentB, mockJdbcTemplate);
        } catch (Exception e) {
            fail("Failed to inject mock: " + e.getMessage());
        }

        // Test scenario
        int userId = 1;
        String newEmail = "mocked@example.com";
        Map<String, Object> expectedUser = new HashMap<>();
        expectedUser.put("id", userId);
        expectedUser.put("email", newEmail);
        expectedUser.put("name", "Test User");

        // Mock behavior
        doNothing().when(mockJdbcTemplate).update(anyString(), anyString(), anyInt());
        when(mockJdbcTemplate.queryForMap(anyString(), anyInt())).thenReturn(expectedUser);

        // Execute
        componentA.updateUserEmail(userId, newEmail);
        Map<String, Object> retrievedUser = componentB.getUserById(userId);

        // Verify
        verify(mockJdbcTemplate).update(anyString(), eq(newEmail), eq(userId));
        verify(mockJdbcTemplate).queryForMap(anyString(), eq(userId));
        assertEquals(expectedUser, retrievedUser);
    }

    @Test
    public void testErrorPropagation() {
        // Setup with mocks
        DataSource mockDataSource = mock(DataSource.class);
        JdbcTemplate mockJdbcTemplate = mock(JdbcTemplate.class);

        ComponentA componentA = new ComponentA(mockDataSource);
        ComponentB componentB = new ComponentB(mockDataSource);

        // Inject mocked JdbcTemplate using reflection
        try {
            java.lang.reflect.Field jdbcField1 = ComponentA.class.getDeclaredField("jdbcTemplate");
            jdbcField1.setAccessible(true);
            jdbcField1.set(componentA, mockJdbcTemplate);

            java.lang.reflect.Field jdbcField2 = ComponentB.class.getDeclaredField("jdbcTemplate");
            jdbcField2.setAccessible(true);
            jdbcField2.set(componentB, mockJdbcTemplate);
        } catch (Exception e) {
            fail("Failed to inject mock: " + e.getMessage());
        }

        // Simulate database error during update
        doThrow(new RuntimeException("Database connection failed")).when(mockJdbcTemplate)
                .update(anyString(), anyString(), anyInt());

        // Execute and verify exception propagation
        int userId = 1;
        String newEmail = "error@example.com";

        Exception exception = assertThrows(RuntimeException.class, () -> {
            componentA.updateUserEmail(userId, newEmail);
        });

        assertEquals("Database connection failed", exception.getMessage());
    }
}