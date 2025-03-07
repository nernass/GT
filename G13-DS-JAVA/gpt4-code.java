import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    public void testCreateAndFetchResource() throws IOException, InterruptedException {
        // Mock data
        String resourceData = "{\"title\": \"foo\", \"body\": \"bar\", \"userId\": 1}";
        String createdResourceId = "101"; // Assuming the created resource ID is 101
        String fetchedResource = "{\"id\": 101, \"title\": \"foo\", \"body\": \"bar\", \"userId\": 1}";

        // Mock ComponentA to return the created resource ID
        ComponentA mockComponentA = Mockito.spy(componentA);
        doReturn(createdResourceId).when(mockComponentA).createResource(resourceData);

        // Create resource using ComponentA
        String resourceId = mockComponentA.createResource(resourceData);
        assertEquals(createdResourceId, resourceId);

        // Mock ComponentB to return the fetched resource
        ComponentB mockComponentB = Mockito.spy(componentB);
        doReturn(fetchedResource).when(mockComponentB).fetchResource(resourceId);

        // Fetch resource using ComponentB
        String fetchedData = mockComponentB.fetchResource(resourceId);
        assertEquals(fetchedResource, fetchedData);

        // Verify the fetched resource details
        boolean isVerified = componentB.verifyResourceDetails(fetchedData, fetchedResource);
        assertTrue(isVerified);
    }
}