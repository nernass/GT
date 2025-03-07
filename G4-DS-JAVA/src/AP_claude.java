import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ComponentIntegrationTest {

    @Mock
    private DataSource mockDataSource;

    @Mock
    private JdbcTemplate mockJdbcTemplate;

    private ComponentA componentA;
    private ComponentB componentB;

    // For tests with real in-memory database
    private EmbeddedDatabase embeddedDatabase;
    private ComponentA realComponentA;
    private ComponentB realComponentB;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup for mocked components
        when(mockDataSource.getConnection()).thenReturn(null); // Not actually used directly in our tests
        componentA = new ComponentA(mockDataSource);
        componentB = new ComponentB(mockDataSource);

        // Inject the mocked JdbcTemplate
        setPrivateField(componentA, "jdbcTemplate", mockJdbcTemplate);
        setPrivateField(componentB, "jdbcTemplate", mockJdbcTemplate);

        // Setup embedded database for real integration tests
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .addScript("classpath:test-data.sql")
                .build();

        realComponentA = new ComponentA(embeddedDatabase);
        realComponentB = new ComponentB(embeddedDatabase);
    }

    // Helper method to set private fields via reflection
    private void setPrivateField(Object object, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }

    @Test
    public void testSuccessfulUserEmailUpdate() {
        // Setup test data
        int userId = 1;
        String newEmail = "updated@example.com";
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", userId);
        userData.put("email", newEmail);
        userData.put("name", "Test User");

        // Configure mocks
        doNothing().when(mockJdbcTemplate).update(anyString(), anyString(), anyInt());
        when(mockJdbcTemplate.queryForMap(anyString(), anyInt())).thenReturn(userData);

        // Execute the workflow: update user email then retrieve the user
        componentA.updateUserEmail(userId, newEmail);
        Map<String, Object> retrievedUser = componentB.getUserById(userId);

        // Verify interactions
        verify(mockJdbcTemplate).update(eq("UPDATE users SET email = ? WHERE id = ?"), eq(newEmail), eq(userId));
        verify(mockJdbcTemplate).queryForMap(eq("SELECT * FROM users WHERE id = ?"), eq(userId));

        // Assert results
        assertEquals(userId, retrievedUser.get("id"));
        assertEquals(newEmail, retrievedUser.get("email"));
    }

    @Test
    public void testFailureHandlingDuringUpdate() {
        // Setup test data
        int userId = 2;
        String newEmail = "willnotupdate@example.com";

        // Configure mock to throw exception during update
        doThrow(new DataAccessException("Database connection failed") {
        })
                .when(mockJdbcTemplate).update(anyString(), anyString(), anyInt());

        // Execute and verify exception is thrown
        assertThrows(DataAccessException.class, () -> {
            componentA.updateUserEmail(userId, newEmail);
        });

        // Verify the update was attempted
        verify(mockJdbcTemplate).update(eq("UPDATE users SET email = ? WHERE id = ?"), eq(newEmail), eq(userId));
    }

    @Test
    public void testUserNotFoundScenario() {
        // Setup test data
        int nonExistentUserId = 999;

        // Configure mock to throw exception when user not found
        when(mockJdbcTemplate.queryForMap(anyString(), eq(nonExistentUserId)))
                .thenThrow(new EmptyResultDataAccessException(1));

        // Execute and verify exception is thrown
        assertThrows(EmptyResultDataAccessException.class, () -> {
            componentB.getUserById(nonExistentUserId);
        });

        // Verify the query was attempted
        verify(mockJdbcTemplate).queryForMap(eq("SELECT * FROM users WHERE id = ?"), eq(nonExistentUserId));
    }

    @Test
    public void testUpdateAndRetrieveWithRealDatabase() {
        // This test requires schema.sql and test-data.sql files with proper setup
        // and assumes there's a user with ID 1 in the test data

        try {
            // Update the user's email
            int userId = 1;
            String newEmail = "realupdate@example.com";
            realComponentA.updateUserEmail(userId, newEmail);

            // Retrieve the user and verify the email was updated
            Map<String, Object> user = realComponentB.getUserById(userId);

            assertEquals(userId, user.get("id"));
            assertEquals(newEmail, user.get("email"));
        } catch (Exception e) {
            fail("Real database integration test failed: " + e.getMessage());
        }
    }

    @Test
    public void testEdgeCaseWithNullEmail() {
        // Setup test data
        int userId = 3;
        String nullEmail = null;

        // Execute and verify - different systems might handle null differently
        // We're just verifying the interaction happens correctly
        assertThrows(Exception.class, () -> {
            componentA.updateUserEmail(userId, nullEmail);
        });
    }
}