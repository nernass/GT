public class ComponentA {

    /**
     * Simulates converting a document from one format to another.
     *
     * @param documentContent The content of the original document.
     * @param targetFormat    The target format to convert to (e.g., "Word").
     * @return The converted document content as a string.
     */
    public String convertDocument(String documentContent, String targetFormat) {
        if (documentContent == null || targetFormat == null) {
            throw new IllegalArgumentException("Document content and target format must not be null.");
        }

        // Simulate document conversion
        System.out.println("Converting document to " + targetFormat + " format...");
        return "Converted to " + targetFormat + ": " + documentContent;
    }
}