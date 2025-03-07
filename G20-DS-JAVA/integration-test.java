import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    @Test
    public void testUniqueIdGenerationAndResourceSaving() {
        // Create instances of ComponentA and ComponentB
        ComponentA idGenerator = new ComponentA();
        ComponentB resourceSaver = new ComponentB();

        // Generate a unique ID using ComponentA
        String uniqueId = idGenerator.generateUniqueId();
        assertNotNull(uniqueId, "Unique ID generation failed");

        // Save the resource with the generated ID using ComponentB
        boolean isResourceSaved = resourceSaver.saveResource(uniqueId);
        assertTrue(isResourceSaved, "Resource saving failed");

        // Verify that the ID is unique by attempting to save it again
        boolean isDuplicateSaved = resourceSaver.saveResource(uniqueId);
        assertFalse(isDuplicateSaved, "Duplicate ID was saved, uniqueness verification failed");
    }
}