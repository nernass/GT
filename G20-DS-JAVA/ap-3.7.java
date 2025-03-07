import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import java.util.Set;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setup() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    @DisplayName("Test successful ID generation and resource saving")
    public void testSuccessfulIntegration() {
        // Generate a unique ID using ComponentA
        String uniqueId = componentA.generateUniqueId();

        // Verify the ID is not null or empty
        Assertions.assertNotNull(uniqueId);
        Assertions.assertFalse(uniqueId.isEmpty());

        // Use ComponentB to save the resource with the generated ID
        boolean saveResult = componentB.saveResource(uniqueId);

        // Verify the save operation was successful
        Assertions.assertTrue(saveResult);

        // Verify the ID was added to the saved resources set
        Set<String> savedIds = componentB.getSavedResourceIds();
        Assertions.assertTrue(savedIds.contains(uniqueId));
        Assertions.assertEquals(1, savedIds.size());
    }

    @Test
    @DisplayName("Test handling duplicate IDs")
    public void testDuplicateIdHandling() {
        // Generate a unique ID using ComponentA
        String uniqueId = componentA.generateUniqueId();

        // Save the resource with the generated ID (first attempt)
        boolean firstSaveResult = componentB.saveResource(uniqueId);
        Assertions.assertTrue(firstSaveResult);

        // Try to save another resource with the same ID (should fail)
        boolean secondSaveResult = componentB.saveResource(uniqueId);
        Assertions.assertFalse(secondSaveResult);

        // Verify only one instance of the ID exists in the saved resources
        Set<String> savedIds = componentB.getSavedResourceIds();
        Assertions.assertEquals(1, savedIds.size());
    }

    @Test
    @DisplayName("Test multiple ID generation and saving")
    public void testMultipleIdGeneration() {
        // Generate and save multiple unique IDs
        int count = 5;
        for (int i = 0; i < count; i++) {
            String uniqueId = componentA.generateUniqueId();
            boolean saveResult = componentB.saveResource(uniqueId);
            Assertions.assertTrue(saveResult);
        }

        // Verify all IDs were saved
        Set<String> savedIds = componentB.getSavedResourceIds();
        Assertions.assertEquals(count, savedIds.size());
    }

    @Test
    @DisplayName("Test error handling with invalid ID")
    public void testInvalidIdHandling() {
        // Test with null ID
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            componentB.saveResource(null);
        });

        // Test with empty ID
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            componentB.saveResource("");
        });

        // Verify no IDs were saved
        Set<String> savedIds = componentB.getSavedResourceIds();
        Assertions.assertTrue(savedIds.isEmpty());
    }
}