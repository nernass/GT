import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    void testSuccessfulDocumentConversionAndVerification() {
        // Setup test data
        String originalContent = "Test document content";
        String targetFormat = "PDF";
        String expectedConvertedContent = "Converted to PDF: Test document content";

        // Perform conversion using ComponentA
        String convertedContent = componentA.convertDocument(originalContent, targetFormat);

        // Verify the conversion result using ComponentB
        boolean verificationResult = componentB.verifyDocumentContent(convertedContent, expectedConvertedContent);

        // Assert
        assertTrue(verificationResult, "Document conversion should be verified successfully");
    }

    @Test
    void testFailedVerification() {
        // Setup test data
        String originalContent = "Test document content";
        String targetFormat = "Word";
        String incorrectExpectedContent = "Wrong expected content";

        // Perform conversion using ComponentA
        String convertedContent = componentA.convertDocument(originalContent, targetFormat);

        // Verify with incorrect expected content using ComponentB
        boolean verificationResult = componentB.verifyDocumentContent(convertedContent, incorrectExpectedContent);

        // Assert
        assertFalse(verificationResult, "Verification should fail with incorrect expected content");
    }

    @Test
    void testNullInputHandling() {
        // Test null input for ComponentA
        Exception exceptionA = assertThrows(IllegalArgumentException.class, () -> {
            componentA.convertDocument(null, "PDF");
        });
        assertEquals("Document content and target format must not be null.", exceptionA.getMessage());

        // Test null input for ComponentB
        Exception exceptionB = assertThrows(IllegalArgumentException.class, () -> {
            componentB.verifyDocumentContent(null, "Some content");
        });
        assertEquals("Converted content and expected content must not be null.", exceptionB.getMessage());

        // Test interaction with null format
        Exception exceptionC = assertThrows(IllegalArgumentException.class, () -> {
            componentA.convertDocument("Content", null);
        });
        assertEquals("Document content and target format must not be null.", exceptionC.getMessage());
    }

    @Test
    void testEdgeCaseWithEmptyString() {
        // Empty string is valid but represents an edge case
        String emptyContent = "";
        String targetFormat = "HTML";
        String expectedConvertedContent = "Converted to HTML: ";

        // Perform conversion
        String convertedContent = componentA.convertDocument(emptyContent, targetFormat);

        // Verify conversion
        boolean verificationResult = componentB.verifyDocumentContent(convertedContent, expectedConvertedContent);

        // Assert
        assertTrue(verificationResult, "Empty string conversion should be handled correctly");
        assertEquals(expectedConvertedContent, convertedContent);
    }

    @Test
    void testEndToEndFlow() {
        // Test data with special characters
        String originalContent = "Special chars: !@#$%^&*()_+";
        String targetFormat = "JSON";

        // Expected conversion result
        String expectedConvertedContent = "Converted to JSON: Special chars: !@#$%^&*()_+";

        // Execute the workflow: ComponentA -> ComponentB
        String convertedContent = componentA.convertDocument(originalContent, targetFormat);
        boolean verificationResult = componentB.verifyDocumentContent(convertedContent, expectedConvertedContent);

        // Assertions
        assertTrue(verificationResult, "End-to-end verification should succeed");
        assertEquals(expectedConvertedContent, convertedContent);
    }
}