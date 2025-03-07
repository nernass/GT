import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    private DataSource dataSource;

    @Mock
    private JdbcTemplate mockJdbcTemplate;

    @BeforeEach
    public void setUp() {
        // Setup for tests using a real embedded database
        dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql") // Create your schema.sql file with users table definition
                .build();

        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);
    }

    @Test
    public void testInsertAndRetrieveUser() {
        // Test scenario: Insert a user with ComponentA and retrieve it with ComponentB
        String testName = "Test User";
        String testEmail = "test@example.com";

        // Insert the user using ComponentA
        componentA.insertUser(testName, testEmail);

        // Retrieve the user using ComponentB
        Map<String, Object> retrievedUser = componentB.getUserByEmail(testEmail);

        // Assert the retrieved user data matches what was inserted
        assertNotNull(retrievedUser, "Retrieved user should not be null");
        assertEquals(testName, retrievedUser.get("name"), "User name should match");
        assertEquals(testEmail, retrievedUser.get("email"), "User email should match");
    }

    @Test
    public void testInsertUserWithMockedComponents() {
        // Test with mocked JdbcTemplate to verify correct interaction between
        // components
        ComponentA mockComponentA = new ComponentA(dataSource) {
            {
                this.jdbcTemplate = mockJdbcTemplate;
            }
        };

        ComponentB mockComponentB = new ComponentB(dataSource) {
            {
                this.jdbcTemplate = mockJdbcTemplate;
            }
        };

        String testName = "Mock User";
        String testEmail = "mock@example.com";

        // Configure mock behavior
        doNothing().when(mockJdbcTemplate).update(anyString(), eq(testName), eq(testEmail));

        Map<String, Object> mockUserData = Map.of(
                "name", testName,
                "email", testEmail,
                "id", 1);

        when(mockJdbcTemplate.queryForMap(anyString(), eq(testEmail))).thenReturn(mockUserData);

        // Execute components
        mockComponentA.insertUser(testName, testEmail);
        Map<String, Object> retrievedUser = mockComponentB.getUserByEmail(testEmail);

        // Verify interactions
        verify(mockJdbcTemplate).update(anyString(), eq(testName), eq(testEmail));
        verify(mockJdbcTemplate).queryForMap(anyString(), eq(testEmail));

        // Verify data
        assertNotNull(retrievedUser);
        assertEquals(testName, retrievedUser.get("name"));
        assertEquals(testEmail, retrievedUser.get("email"));
    }

    @Test
    public void testErrorHandling() {
        // Test error scenario when database operations fail
        ComponentA mockComponentA = new ComponentA(dataSource) {
            {
                this.jdbcTemplate = mockJdbcTemplate;
            }
        };

        ComponentB mockComponentB = new ComponentB(dataSource) {
            {
                this.jdbcTemplate = mockJdbcTemplate;
            }
        };

        String testName = "Error User";
        String testEmail = "error@example.com";

        // Configure mock to throw an exception on insert
        doThrow(new RuntimeException("Database error")).when(mockJdbcTemplate)
                .update(anyString(), eq(testName), eq(testEmail));

        // Verify exception is propagated
        Exception exception = assertThrows(RuntimeException.class,
                () -> mockComponentA.insertUser(testName, testEmail));

        assertEquals("Database error", exception.getMessage());

        // Even after error, verify getUserByEmail would be called correctly
        when(mockJdbcTemplate.queryForMap(anyString(), eq(testEmail)))
                .thenReturn(Map.of("name", testName, "email", testEmail));

        Map<String, Object> user = mockComponentB.getUserByEmail(testEmail);
        assertEquals(testEmail, user.get("email"));
    }

    @Test
    public void testEdgeCases() {
        // Test with null values
        ComponentA mockComponentA = new ComponentA(dataSource) {
            {
                this.jdbcTemplate = mockJdbcTemplate;
            }
        };

        // Null name - should still execute with null parameter
        doNothing().when(mockJdbcTemplate).update(anyString(), eq(null), eq("email@example.com"));
        mockComponentA.insertUser(null, "email@example.com");
        verify(mockJdbcTemplate).update(anyString(), eq(null), eq("email@example.com"));

        // Null email - should still execute with null parameter
        doNothing().when(mockJdbcTemplate).update(anyString(), eq("Test Name"), eq(null));
        mockComponentA.insertUser("Test Name", null);
        verify(mockJdbcTemplate).update(anyString(), eq("Test Name"), eq(null));

        // Test with empty string values
        doNothing().when(mockJdbcTemplate).update(anyString(), eq(""), eq(""));
        mockComponentA.insertUser("", "");
        verify(mockJdbcTemplate).update(anyString(), eq(""), eq(""));
    }

    @AfterEach
    public void tearDown() {
        // Clean up the embedded database
        if (dataSource instanceof EmbeddedDatabase) {
            ((EmbeddedDatabase) dataSource).shutdown();
        }
    }
}