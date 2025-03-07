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
    @DisplayName("Generated ID should be successfully saved in ComponentB")
    void generatedIdShouldBeSavedSuccessfully() {
        // Act
        String uniqueId = componentA.generateUniqueId();
        boolean saveResult = componentB.saveResource(uniqueId);

        // Assert
        assertTrue(saveResult, "The generated ID should be saved successfully");
        assertTrue(componentB.getSavedResourceIds().contains(uniqueId), "The ID should be present in saved resources");
    }

    @Test
    @DisplayName("Multiple generated IDs should all be unique and saved")
    void multipleGeneratedIdsShouldAllBeUnique() {
        // Act
        String id1 = componentA.generateUniqueId();
        String id2 = componentA.generateUniqueId();
        String id3 = componentA.generateUniqueId();

        boolean saved1 = componentB.saveResource(id1);
        boolean saved2 = componentB.saveResource(id2);
        boolean saved3 = componentB.saveResource(id3);

        // Assert
        assertTrue(saved1 && saved2 && saved3, "All generated IDs should be saved successfully");
        assertEquals(3, componentB.getSavedResourceIds().size(), "There should be 3 unique IDs saved");
    }

    @Test
    @DisplayName("Attempting to save the same ID twice should fail")
    void savingSameIdTwiceShouldFail() {
        // Arrange
        String uniqueId = componentA.generateUniqueId();

        // Act
        boolean firstSave = componentB.saveResource(uniqueId);
        boolean secondSave = componentB.saveResource(uniqueId);

        // Assert
        assertTrue(firstSave, "First save should succeed");
        assertFalse(secondSave, "Second save with same ID should fail");
        assertEquals(1, componentB.getSavedResourceIds().size(), "Only one ID should be saved");
    }

    @Test
    @DisplayName("Saving resource with null ID should throw IllegalArgumentException")
    void savingNullIdShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            componentB.saveResource(null);
        }, "Saving null ID should throw IllegalArgumentException");
    }

    @Test
    @DisplayName("Saving resource with empty ID should throw IllegalArgumentException")
    void savingEmptyIdShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            componentB.saveResource("");
        }, "Saving empty ID should throw IllegalArgumentException");
    }

    @Test
    @DisplayName("Generated IDs should be in UUID format")
    void generatedIdsShouldBeInUuidFormat() {
        // Act
        String uniqueId = componentA.generateUniqueId();

        // Assert
        assertNotNull(uniqueId, "Generated ID should not be null");
        assertEquals(36, uniqueId.length(), "UUID should be 36 characters long");
        assertTrue(uniqueId.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"),
                "Generated ID should match UUID format");
    }
}