import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    public void testGenerateAndSaveUniqueId() {
        String uniqueId = componentA.generateUniqueId();
        assertTrue(componentB.saveResource(uniqueId));
        assertTrue(componentB.getSavedResourceIds().contains(uniqueId));
    }

    @Test
    public void testSaveDuplicateId() {
        String uniqueId = componentA.generateUniqueId();
        assertTrue(componentB.saveResource(uniqueId));
        assertFalse(componentB.saveResource(uniqueId));
    }

    @Test
    public void testSaveNullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            componentB.saveResource(null);
        });
        assertEquals("Resource ID must not be null or empty.", exception.getMessage());
    }

    @Test
    public void testSaveEmptyId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            componentB.saveResource("");
        });
        assertEquals("Resource ID must not be null or empty.", exception.getMessage());
    }
}