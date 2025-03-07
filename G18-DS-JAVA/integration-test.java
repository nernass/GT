import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    @Test
    public void testDocumentConversionAndVerification() {
        // Create instances of ComponentA and ComponentB
        ComponentA documentConverter = new ComponentA();
        ComponentB documentVerifier = new ComponentB();

        // Test data
        String originalContent = "This is a sample document.";
        String targetFormat = "Word";
        String expectedConvertedContent = "Converted to Word: This is a sample document.";

        // Convert the document using ComponentA
        String convertedContent = documentConverter.convertDocument(originalContent, targetFormat);
        assertNotNull(convertedContent, "Document conversion failed");

        // Verify the converted document's content using ComponentB
        boolean isContentVerified = documentVerifier.verifyDocumentContent(convertedContent, expectedConvertedContent);
        assertTrue(isContentVerified, "Document content verification failed");
    }
}