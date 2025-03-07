import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    void testSuccessfulResourceCreationAndStorage() {
        // Generate ID using ComponentA
        String uniqueId = componentA.generateUniqueId();

        // Verify ID is not null or empty
        assertNotNull(uniqueId);
        assertFalse(uniqueId.isEmpty());

        // Save resource using ComponentB
        boolean saveResult = componentB.saveResource(uniqueId);

        // Verify save was successful
        assertTrue(saveResult);
        assertTrue(componentB.getSavedResourceIds().contains(uniqueId));
    }

    @Test
    void testDuplicateResourceCreation() {
        // Generate and save first resource
        String firstId = componentA.generateUniqueId();
        assertTrue(componentB.saveResource(firstId));

        // Attempt to save resource with same ID
        assertFalse(componentB.saveResource(firstId));

        // Verify only one instance exists
        assertEquals(1, componentB.getSavedResourceIds().size());
    }

    @Test
    void testMultipleResourceCreation() {
        // Generate and save multiple resources
        String[] ids = new String[5];
        for (int i = 0; i < 5; i++) {
            ids[i] = componentA.generateUniqueId();
            assertTrue(componentB.saveResource(ids[i]));
        }

        // Verify all resources were saved
        assertEquals(5, componentB.getSavedResourceIds().size());
        for (String id : ids) {
            assertTrue(componentB.getSavedResourceIds().contains(id));
        }
    }

    @Test
    void testInvalidResourceId() {
        assertThrows(IllegalArgumentException.class, () -> {
            componentB.saveResource(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            componentB.saveResource("");
        });
    }
}