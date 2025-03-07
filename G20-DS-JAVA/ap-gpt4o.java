import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ComponentIntegrationTest {

    @Mock
    private ComponentB componentB;

    @InjectMocks
    private ComponentA componentA;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSuccessPath() {
        // Arrange
        String generatedId = "unique-id-123";
        when(componentA.generateUniqueId()).thenReturn(generatedId);
        when(componentB.saveResource(generatedId)).thenReturn(true);

        // Act
        String uniqueId = componentA.generateUniqueId();
        boolean result = componentB.saveResource(uniqueId);

        // Assert
        assertEquals(generatedId, uniqueId);
        assertTrue(result);
        verify(componentB).saveResource(generatedId);
    }

    @Test
    public void testPartialFailure() {
        // Arrange
        String generatedId = "unique-id-123";
        when(componentA.generateUniqueId()).thenReturn(generatedId);
        when(componentB.saveResource(generatedId)).thenReturn(false);

        // Act
        String uniqueId = componentA.generateUniqueId();
        boolean result = componentB.saveResource(uniqueId);

        // Assert
        assertEquals(generatedId, uniqueId);
        assertFalse(result);
        verify(componentB).saveResource(generatedId);
    }

    @Test
    public void testEdgeCaseEmptyInput() {
        // Arrange
        String generatedId = "";
        when(componentA.generateUniqueId()).thenReturn(generatedId);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            componentB.saveResource(generatedId);
        });
    }
}