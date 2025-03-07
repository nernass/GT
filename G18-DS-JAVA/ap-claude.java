java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

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
        // Arrange
        String originalContent = "Sample document content";
        String targetFormat = "PDF";
        String expectedConvertedContent = "Converted to PDF: Sample document content";
        
        // Act - Integration point between ComponentA and ComponentB
        String convertedContent = componentA.convertDocument(originalContent, targetFormat);
        boolean verificationResult = componentB.verifyDocumentContent(convertedContent, expectedConvertedContent);
        
        // Assert
        assertTrue(verificationResult, "Document conversion and verification should succeed");
    }
    
    @Test
    void testFailedVerificationWhenContentDoesNotMatch() {
        // Arrange
        String originalContent = "Sample document content";
        String targetFormat = "Word";
        String incorrectExpectedContent = "Wrong expected content";
        
        // Act - Integration point between ComponentA and ComponentB
        String convertedContent = componentA.convertDocument(originalContent, targetFormat);
        boolean verificationResult = componentB.verifyDocumentContent(convertedContent, incorrectExpectedContent);
        
        // Assert
        assertFalse(verificationResult, "Verification should fail when expected content doesn't match");
    }
    
    @Test
    void testNullDocumentContentPropagation() {
        // Arrange
        String targetFormat = "PDF";
        
        // Act & Assert - Verify exception propagates through the integration
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            String convertedContent = componentA.convertDocument(null, targetFormat);
            // This should never execute since the previous line throws an exception
            componentB.verifyDocumentContent(convertedContent, "Some expected content");
        });
        
        assertTrue(exception.getMessage().contains("Document content and target format must not be null"));
    }
    
    @Test
    void testNullTargetFormatPropagation() {
        // Arrange
        String originalContent = "Sample document content";
        
        // Act & Assert - Verify exception propagates through the integration
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            String convertedContent = componentA.convertDocument(originalContent, null);
            // This should never execute since the previous line throws an exception
            componentB.verifyDocumentContent(convertedContent, "Some expected content");
        });
        
        assertTrue(exception.getMessage().contains("Document content and target format must not be null"));
    }
    
    @ParameterizedTest
    @MethodSource("provideFormatsAndContents")
    void testMultipleFormatsAndContents(String content, String format, String expectedContent) {
        // Act - Integration point between ComponentA and ComponentB
        String convertedContent = componentA.convertDocument(content, format);
        boolean verificationResult = componentB.verifyDocumentContent(convertedContent, expectedContent);
        
        // Assert
        assertTrue(verificationResult, 
            String.format("Content '%s' converted to %s format should match '%s'", 
                content, format, expectedContent));
    }
    
    static Stream<Arguments> provideFormatsAndContents() {
        return Stream.of(
            Arguments.of("Short text", "PDF", "Converted to PDF: Short text"),
            Arguments.of("", "Word", "Converted to Word: "),
            Arguments.of("Special chars: !@#$%^", "XML", "Converted to XML: Special chars: !@#$%^"),
            Arguments.of("Very long document content that might exceed certain limits in real-world scenarios", 
                         "HTML", 
                         "Converted to HTML: Very long document content that might exceed certain limits in real-world scenarios")
        );
    }
    
    @Test
    void testEndToEndWorkflow() {
        // This test verifies a complete workflow involving both components
        
        // Arrange
        String originalContent = "Important business document";
        String targetFormat = "Excel";
        
        // Act - First part of integration
        String convertedContent = componentA.convertDocument(originalContent, targetFormat);
        
        // Second part - verify the converted content format is as expected
        String expectedPattern = "Converted to " + targetFormat + ": " + originalContent;
        boolean formatVerification = componentB.verifyDocumentContent(convertedContent, expectedPattern);
        
        // Assert
        assertTrue(formatVerification, "The end-to-end document workflow should complete successfully");
    }
}