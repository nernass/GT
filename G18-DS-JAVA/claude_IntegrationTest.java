import org.junit.jupiter.api.*;
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
    void testSuccessfulDocumentConversionAndVerification() {
        String originalContent = "Hello World";
        String targetFormat = "PDF";

        // Convert document using ComponentA
        String convertedContent = componentA.convertDocument(originalContent, targetFormat);
        String expectedContent = "Converted to PDF: Hello World";

        // Verify conversion using ComponentB
        boolean isValid = componentB.verifyDocumentContent(convertedContent, expectedContent);
        assertTrue(isValid, "Document conversion and verification should succeed");
    }

    @Test
    void testNullDocumentContent() {
        String targetFormat = "Word";

        assertThrows(IllegalArgumentException.class, () -> {
            componentA.convertDocument(null, targetFormat);
        }, "Should throw IllegalArgumentException for null document content");

        assertThrows(IllegalArgumentException.class, () -> {
            componentB.verifyDocumentContent(null, "some content");
        }, "Should throw IllegalArgumentException for null converted content");
    }

    @Test
    void testNullTargetFormat() {
        String originalContent = "Test Content";

        assertThrows(IllegalArgumentException.class, () -> {
            componentA.convertDocument(originalContent, null);
        }, "Should throw IllegalArgumentException for null target format");
    }

    @Test
    void testMismatchedContent() {
        String originalContent = "Original Content";
        String targetFormat = "Word";

        String convertedContent = componentA.convertDocument(originalContent, targetFormat);
        String wrongExpectedContent = "Different Content";

        boolean isValid = componentB.verifyDocumentContent(convertedContent, wrongExpectedContent);
        assertFalse(isValid, "Verification should fail for mismatched content");
    }
}