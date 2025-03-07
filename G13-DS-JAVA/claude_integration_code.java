import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    private static final String TEST_RESOURCE_DATA = "{\"title\":\"test title\",\"body\":\"test body\",\"userId\":1}";
    private static final String TEST_RESOURCE_RESPONSE = "{\"id\":101,\"title\":\"test title\",\"body\":\"test body\",\"userId\":1}";

    @BeforeEach
    void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    void testCreateAndFetchResource() throws IOException, InterruptedException {
        // Create resource using ComponentA
        String createdResourceResponse = componentA.createResource(TEST_RESOURCE_DATA);
        assertNotNull(createdResourceResponse);

        // Extract ID from created resource response (assuming response contains ID 101)
        String resourceId = "101";

        // Fetch created resource using ComponentB
        String fetchedResource = componentB.fetchResource(resourceId);
        assertNotNull(fetchedResource);

        // Verify resource details
        boolean isMatching = componentB.verifyResourceDetails(fetchedResource, TEST_RESOURCE_RESPONSE);
        assertTrue(isMatching);
    }

    @Test
    void testErrorHandling() {
        // Test with invalid resource ID
        Exception exception = assertThrows(IOException.class, () -> {
            componentB.fetchResource("invalid_id");
        });
        assertNotNull(exception);
    }

    @Test
    void testInvalidResourceData() {
        String invalidData = "invalid_json_data";
        Exception exception = assertThrows(IOException.class, () -> {
            componentA.createResource(invalidData);
        });
        assertNotNull(exception);
    }

    @Test
    void testSequentialOperations() throws IOException, InterruptedException {
        // Create multiple resources and verify them sequentially
        String firstResource = componentA.createResource(TEST_RESOURCE_DATA);
        String secondResource = componentA.createResource(TEST_RESOURCE_DATA);

        assertNotEquals(firstResource, secondResource);

        // Verify both resources can be fetched
        String firstId = "101";
        String secondId = "102";

        assertNotNull(componentB.fetchResource(firstId));
        assertNotNull(componentB.fetchResource(secondId));
    }
}