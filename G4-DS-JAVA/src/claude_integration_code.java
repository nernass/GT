import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

public class ComponentABIntegrationTest {

    private EmbeddedDatabase dataSource;
    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    void setUp() {
        dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .addScript("test-data.sql")
                .build();

        componentA = new ComponentA(dataSource);
        componentB = new ComponentB(dataSource);
    }

    @Test
    void testUpdateAndRetrieveUserEmail() {
        // Given
        int userId = 1;
        String newEmail = "newemail@test.com";

        // When
        componentA.updateUserEmail(userId, newEmail);
        Map<String, Object> updatedUser = componentB.getUserById(userId);

        // Then
        assertEquals(newEmail, updatedUser.get("email"));
    }

    @Test
    void testUpdateNonExistentUser() {
        // Given
        int nonExistentUserId = 999;
        String newEmail = "newemail@test.com";

        // Then
        assertThrows(Exception.class, () -> {
            componentA.updateUserEmail(nonExistentUserId, newEmail);
        });
    }

    @Test
    void testRetrieveNonExistentUser() {
        // Given
        int nonExistentUserId = 999;

        // Then
        assertThrows(Exception.class, () -> {
            componentB.getUserById(nonExistentUserId);
        });
    }

    @AfterEach
    void tearDown() {
        dataSource.shutdown();
    }
}