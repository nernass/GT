import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

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
    @DisplayName("Test successful document conversion and verification")
    void testSuccessfulDocumentConversionAndVerification() {
        // Test data
        String originalContent = "Hello World";
        String targetFormat = "PDF";

        // Process through ComponentA
        String convertedContent = componentA.convertDocument(originalContent, targetFormat);

        // Expected result after conversion
        String expectedContent = "Converted to PDF: Hello World";

        // Verify using ComponentB
        boolean verificationResult = componentB.verifyDocumentContent(convertedContent, expectedContent);

        // Assert
        assertTrue(verificationResult, "Document conversion and verification should succeed");
    }

    @Test
    @DisplayName("Test failed verification when content doesn't match expected")
    void testFailedVerification() {
        // Test data
        String originalContent = "Hello World";
        String targetFormat = "Word";

        // Process through ComponentA
        String convertedContent = componentA.convertDocument(originalContent, targetFormat);

        // Incorrect expected result
        String expectedContent = "Wrong content";

        // Verify using ComponentB
        boolean verificationResult = componentB.verifyDocumentContent(convertedContent, expectedContent);

        // Assert
        assertFalse(verificationResult, "Verification should fail with incorrect expected content");
    }

    @ParameterizedTest
    @DisplayName("Test exception handling for null document content")
    @NullAndEmptySource
    void testNullAndEmptyDocumentContent(String documentContent) {
        String targetFormat = "PDF";

        if (documentContent == null) {
            // Test null document content
            assertThrows(IllegalArgumentException.class, () -> {
                componentA.convertDocument(documentContent, targetFormat);
            }, "Should throw IllegalArgumentException for null document content");
        } else {
            // Empty string is valid input, should process normally
            String convertedContent = componentA.convertDocument(documentContent, targetFormat);
            String expectedContent = "Converted to PDF: ";

            boolean verificationResult = componentB.verifyDocumentContent(convertedContent, expectedContent);
            assertTrue(verificationResult, "Empty document should be processed correctly");
        }
    }

    @Test
    @DisplayName("Test exception handling for null target format")
    void testNullTargetFormat() {
        String documentContent = "Hello World";
        String targetFormat = null;

        assertThrows(IllegalArgumentException.class, () -> {
            componentA.convertDocument(documentContent, targetFormat);
        }, "Should throw IllegalArgumentException for null target format");
    }

    @Test
    @DisplayName("Test exception handling for null verification inputs")
    void testNullVerificationInputs() {
        // Test null converted content
        assertThrows(IllegalArgumentException.class, () -> {
            componentB.verifyDocumentContent(null, "Expected content");
        }, "Should throw IllegalArgumentException for null converted content");

        // Test null expected content
        assertThrows(IllegalArgumentException.class, () -> {
            componentB.verifyDocumentContent("Converted content", null);
        }, "Should throw IllegalArgumentException for null expected content");
    }

    @ParameterizedTest
    @DisplayName("Test document conversion with different target formats")
    @ValueSource(strings = { "PDF", "Word", "HTML", "Text" })
    void testDocumentConversionWithDifferentFormats(String format) {
        String originalContent = "Test content";

        // Process through ComponentA
        String convertedContent = componentA.convertDocument(originalContent, format);

        // Expected result
        String expectedContent = "Converted to " + format + ": Test content";

        // Verify using ComponentB
        boolean verificationResult = componentB.verifyDocumentContent(convertedContent, expectedContent);

        // Assert
        assertTrue(verificationResult, "Document conversion should work for format: " + format);
    }
}