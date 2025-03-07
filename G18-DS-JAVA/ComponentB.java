public class ComponentB {

    /**
     * Verifies the content of the converted document.
     *
     * @param convertedContent The content of the converted document.
     * @param expectedContent  The expected content after conversion.
     * @return true if the converted content matches the expected content, false
     *         otherwise.
     */
    public boolean verifyDocumentContent(String convertedContent, String expectedContent) {
        if (convertedContent == null || expectedContent == null) {
            throw new IllegalArgumentException("Converted content and expected content must not be null.");
        }

        // Verify the converted content
        return convertedContent.equals(expectedContent);
    }
}