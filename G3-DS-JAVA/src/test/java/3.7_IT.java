import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    private DataSource dataSource;
    private EmbeddedDatabase db;

    @Mock
    private JdbcTemplate mockJdbcTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up an embedded database for integration testing
        db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .build();

        dataSource = db;

        // Initialize components with real database for full integration test
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);
    }

    @Test
    void testComponentIntegration() {
        // Test data
        String name = "John Doe";
        String email = "john.doe@example.com";

        // Insert a user with ComponentA
        componentA.insertUser(name, email);

        // Retrieve the user with ComponentB
        Map<String, Object> user = componentB.getUserByEmail(email);

        // Verify the retrieved user has the expected data
        assertNotNull(user);
        assertEquals(name, user.get("name"));
        assertEquals(email, user.get("email"));
    }

    @Test
    void testComponentIntegrationWithMocks() {
        // Using mocks for isolation testing
        ComponentA mockCompA = new ComponentA(mock(DataSource.class));
        ComponentB mockCompB = new ComponentB(mock(DataSource.class));

        // Inject mocked JdbcTemplate using reflection
        try {
            java.lang.reflect.Field fieldA = ComponentA.class.getDeclaredField("jdbcTemplate");
            fieldA.setAccessible(true);
            fieldA.set(mockCompA, mockJdbcTemplate);

            java.lang.reflect.Field fieldB = ComponentB.class.getDeclaredField("jdbcTemplate");
            fieldB.setAccessible(true);
            fieldB.set(mockCompB, mockJdbcTemplate);
        } catch (Exception e) {
            fail("Failed to set up mocks: " + e.getMessage());
        }

        // Test data
        String name = "Jane Doe";
        String email = "jane.doe@example.com";

        // Set up mock behavior
        doNothing().when(mockJdbcTemplate).update(anyString(), any(), any());

        Map<String, Object> mockUser = new HashMap<>();
        mockUser.put("name", name);
        mockUser.put("email", email);
        when(mockJdbcTemplate.queryForMap(anyString(), eq(email))).thenReturn(mockUser);

        // Execute the methods
        mockCompA.insertUser(name, email);
        Map<String, Object> retrievedUser = mockCompB.getUserByEmail(email);

        // Verify interactions and results
        verify(mockJdbcTemplate).update(anyString(), eq(name), eq(email));
        verify(mockJdbcTemplate).queryForMap(anyString(), eq(email));
        assertEquals(name, retrievedUser.get("name"));
        assertEquals(email, retrievedUser.get("email"));
    }

    @Test
    void testErrorHandling() {
        // Test with invalid data that should raise an exception
        ComponentA mockCompA = new ComponentA(mock(DataSource.class));
        ComponentB mockCompB = new ComponentB(mock(DataSource.class));

        // Inject mocked JdbcTemplate
        try {
            java.lang.reflect.Field fieldA = ComponentA.class.getDeclaredField("jdbcTemplate");
            fieldA.setAccessible(true);
            fieldA.set(mockCompA, mockJdbcTemplate);

            java.lang.reflect.Field fieldB = ComponentB.class.getDeclaredField("jdbcTemplate");
            fieldB.setAccessible(true);
            fieldB.set(mockCompB, mockJdbcTemplate);
        } catch (Exception e) {
            fail("Failed to set up mocks: " + e.getMessage());
        }

        // Set up the mock to throw an exception
        when(mockJdbcTemplate.queryForMap(anyString(), eq("nonexistent@example.com")))
                .thenThrow(new org.springframework.dao.EmptyResultDataAccessException(1));

        // Verify exception is thrown when looking up non-existent user
        assertThrows(org.springframework.dao.EmptyResultDataAccessException.class, () -> {
            mockCompB.getUserByEmail("nonexistent@example.com");
        });
    }
}