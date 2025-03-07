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
    public void testDocumentConversionAndVerification() {
        String originalContent = "This is a test document.";
        String targetFormat = "Word";
        String expectedConvertedContent = "Converted to Word: This is a test document.";

        // Convert the document using ComponentA
        String convertedContent = componentA.convertDocument(originalContent, targetFormat);

        // Verify the converted content using ComponentB
        boolean isContentVerified = componentB.verifyDocumentContent(convertedContent, expectedConvertedContent);

        // Assert that the content is verified successfully
        assertTrue(isContentVerified, "The converted content should match the expected content.");
    }

    @Test
    public void testNullDocumentContent() {
        String targetFormat = "Word";

        // Expect IllegalArgumentException when document content is null
        assertThrows(IllegalArgumentException.class, () -> {
            componentA.convertDocument(null, targetFormat);
        });
    }

    @Test
    public void testNullTargetFormat() {
        String originalContent = "This is a test document.";

        // Expect IllegalArgumentException when target format is null
        assertThrows(IllegalArgumentException.class, () -> {
            componentA.convertDocument(originalContent, null);
        });
    }

    @Test
    public void testNullConvertedContentInVerification() {
        String expectedContent = "Converted to Word: This is a test document.";

        // Expect IllegalArgumentException when converted content is null
        assertThrows(IllegalArgumentException.class, () -> {
            componentB.verifyDocumentContent(null, expectedContent);
        });
    }

    @Test
    public void testNullExpectedContentInVerification() {
        String convertedContent = "Converted to Word: This is a test document.";

        // Expect IllegalArgumentException when expected content is null
        assertThrows(IllegalArgumentException.class, () -> {
            componentB.verifyDocumentContent(convertedContent, null);
        });
    }
}