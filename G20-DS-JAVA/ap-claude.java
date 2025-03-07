import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    @DisplayName("Success path: ComponentA generates ID and ComponentB saves resource successfully")
    void testSuccessfulIntegration() {
        // Act: ComponentA generates ID
        String uniqueId = componentA.generateUniqueId();

        // Act: ComponentB saves resource with generated ID
        boolean saved = componentB.saveResource(uniqueId);

        // Assert: Resource was saved successfully
        assertTrue(saved);
        assertTrue(componentB.getSavedResourceIds().contains(uniqueId));
        assertEquals(1, componentB.getSavedResourceIds().size());
    }

    @Test
    @DisplayName("Multiple resources: ComponentA generates multiple unique IDs that ComponentB saves")
    void testMultipleResourcesIntegration() {
        // Act: Generate and save multiple resources
        String id1 = componentA.generateUniqueId();
        String id2 = componentA.generateUniqueId();
        String id3 = componentA.generateUniqueId();

        boolean saved1 = componentB.saveResource(id1);
        boolean saved2 = componentB.saveResource(id2);
        boolean saved3 = componentB.saveResource(id3);

        // Assert: All resources were saved successfully
        assertTrue(saved1);
        assertTrue(saved2);
        assertTrue(saved3);
        assertEquals(3, componentB.getSavedResourceIds().size());
        assertTrue(componentB.getSavedResourceIds().contains(id1));
        assertTrue(componentB.getSavedResourceIds().contains(id2));
        assertTrue(componentB.getSavedResourceIds().contains(id3));
    }

    @Test
    @DisplayName("Duplicate handling: ComponentB correctly rejects duplicate IDs from ComponentA")
    void testDuplicateIdRejection() {
        // Arrange: Generate a unique ID with ComponentA
        String uniqueId = componentA.generateUniqueId();

        // Act: Save the resource twice
        boolean firstSave = componentB.saveResource(uniqueId);
        boolean secondSave = componentB.saveResource(uniqueId);

        // Assert: First save succeeds, second save fails
        assertTrue(firstSave);
        assertFalse(secondSave);
        assertEquals(1, componentB.getSavedResourceIds().size());
    }

    @Test
    @DisplayName("Edge case: ComponentB handles invalid IDs properly")
    void testInvalidIdHandling() {
        // Generate a valid ID first to verify normal operation
        String validId = componentA.generateUniqueId();
        boolean validSave = componentB.saveResource(validId);
        assertTrue(validSave);

        // Assert: Saving null ID throws exception
        assertThrows(IllegalArgumentException.class, () -> componentB.saveResource(null));

        // Assert: Saving empty ID throws exception
        assertThrows(IllegalArgumentException.class, () -> componentB.saveResource(""));

        // Verify only the valid resource was saved
        assertEquals(1, componentB.getSavedResourceIds().size());
    }
}