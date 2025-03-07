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
    public void testValidInput_AllComponentsSucceed() {
        // Arrange
        String documentContent = "Original Document";
        String targetFormat = "Word";
        String convertedContent = "Converted to Word: Original Document";
        String expectedContent = "Converted to Word: Original Document";

        when(componentB.verifyDocumentContent(convertedContent, expectedContent)).thenReturn(true);

        // Act
        String result = componentA.convertDocument(documentContent, targetFormat);
        boolean isVerified = componentB.verifyDocumentContent(result, expectedContent);

        // Assert
        assertEquals(convertedContent, result);
        assertTrue(isVerified);
    }

    @Test
    public void testComponentBFailure() {
        // Arrange
        String documentContent = "Original Document";
        String targetFormat = "Word";
        String convertedContent = "Converted to Word: Original Document";
        String expectedContent = "Converted to Word: Original Document";

        when(componentB.verifyDocumentContent(convertedContent, expectedContent))
                .thenThrow(new RuntimeException("Verification failed"));

        // Act & Assert
        String result = componentA.convertDocument(documentContent, targetFormat);
        assertThrows(RuntimeException.class, () -> {
            componentB.verifyDocumentContent(result, expectedContent);
        });
    }

    @Test
    public void testInvalidInputToComponentA() {
        // Arrange
        String documentContent = null;
        String targetFormat = "Word";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            componentA.convertDocument(documentContent, targetFormat);
        });
    }
}