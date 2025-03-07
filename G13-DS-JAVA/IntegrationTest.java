
// IntegrationTest.java
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        // Initialize components
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    public void testIntegration() throws IOException, InterruptedException {
        // Test data
        String resourceData = "{\"title\": \"Test Post\", \"body\": \"This is a test post.\", \"userId\": 1}";
        String expectedData = "{\"title\": \"Test Post\", \"body\": \"This is a test post.\", \"userId\": 1}";

        // Step 1: Create a new resource using Component A
        String createdResource = componentA.createResource(resourceData);

        // Step 2: Fetch the created resource using Component B
        String fetchedResource = componentB.fetchResource(extractResourceId(createdResource));

        // Step 3: Verify that the fetched resource matches the expected data
        boolean isVerified = componentB.verifyResourceDetails(fetchedResource, expectedData);
        assertTrue(isVerified, "The fetched resource does not match the expected data");
    }

    /**
     * Extracts the resource ID from the created resource response (for simplicity,
     * we assume the response contains the ID).
     *
     * @param createdResource The response from the POST request.
     * @return The resource ID.
     */
    private String extractResourceId(String createdResource) {
        // For simplicity, we assume the response is in JSON format and contains an "id"
        // field
        return createdResource.split("\"id\":")[1].split(",")[0].trim();
    }
}