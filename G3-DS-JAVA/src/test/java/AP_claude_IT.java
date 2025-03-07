import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.DuplicateKeyException;

import javax.sql.DataSource;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    private EmbeddedDatabase dataSource;

    @BeforeEach
    public void setup() {
        // Setup in-memory database for testing
        dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql") // Create users table
                .build();

        // Initialize components with the same datasource
        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);
    }

    @Test
    public void testInsertAndRetrieveUser() {
        // Test data
        String name = "John Doe";
        String email = "john.doe@example.com";

        // Use ComponentA to insert a user
        componentA.insertUser(name, email);

        // Use ComponentB to retrieve the user
        Map<String, Object> user = componentB.getUserByEmail(email);

        // Verify the data
        assertNotNull(user);
        assertEquals(name, user.get("name"));
        assertEquals(email, user.get("email"));
    }

    @Test
    public void testMultipleUserInsertions() {
        // Insert multiple users and verify they can be retrieved individually
        componentA.insertUser("Alice Smith", "alice@example.com");
        componentA.insertUser("Bob Johnson", "bob@example.com");

        // Retrieve and verify first user
        Map<String, Object> user1 = componentB.getUserByEmail("alice@example.com");
        assertEquals("Alice Smith", user1.get("name"));

        // Retrieve and verify second user
        Map<String, Object> user2 = componentB.getUserByEmail("bob@example.com");
        assertEquals("Bob Johnson", user2.get("name"));
    }

    @Test
    public void testNonExistentUser() {
        // Attempt to retrieve a user that does not exist
        assertThrows(EmptyResultDataAccessException.class, () -> {
            componentB.getUserByEmail("nonexistent@example.com");
        });
    }

    @Test
    public void testDuplicateEmail() {
        // Insert a user
        componentA.insertUser("Original User", "duplicate@example.com");

        // Attempt to insert another user with the same email
        // Note: This assumes email is a unique key in the database
        assertThrows(DuplicateKeyException.class, () -> {
            componentA.insertUser("Duplicate User", "duplicate@example.com");
        });
    }
}