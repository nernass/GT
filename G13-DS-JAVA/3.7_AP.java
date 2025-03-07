import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        componentA = new ComponentA() {
            @Override
            protected HttpClient createHttpClient() {
                return mockHttpClient;
            }
        };
        componentB = new ComponentB() {
            @Override
            protected HttpClient createHttpClient() {
                return mockHttpClient;
            }
        };
    }

    @Test
    public void testSuccessfulCreateAndFetchResourceFlow() throws IOException, InterruptedException {
        // Test data
        String resourceData = "{\"title\":\"Test Title\",\"body\":\"Test Body\",\"userId\":1}";
        String createdResourceResponse = "{\"id\":101,\"title\":\"Test Title\",\"body\":\"Test Body\",\"userId\":1}";
        String resourceId = "101";

        // Mock HTTP responses
        when(mockResponse.body()).thenReturn(createdResourceResponse);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        // Create resource using ComponentA
        String creationResult = componentA.createResource(resourceData);

        // Verify resource creation
        assertNotNull(creationResult);
        assertEquals(createdResourceResponse, creationResult);

        // Extract resource ID from response (in real scenario)
        // Here we're manually setting it based on test data

        // Fetch resource using ComponentB
        String fetchedResource = componentB.fetchResource(resourceId);

        // Verify resource retrieval
        assertNotNull(fetchedResource);

        // Test verification of resource details
        boolean verificationResult = componentB.verifyResourceDetails(fetchedResource, createdResourceResponse);
        assertTrue(verificationResult);

        // Verify HTTP client was called twice (once for each component)
        verify(mockHttpClient, times(2)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    public void testFailureInResourceCreation() throws IOException, InterruptedException {
        // Test data
        String invalidResourceData = "{\"invalid\":\"data\"}";

        // Mock HTTP response to simulate failure in ComponentA
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Connection refused"));

        // Test ComponentA with failure
        Exception exception = assertThrows(IOException.class, () -> {
            componentA.createResource(invalidResourceData);
        });

        assertEquals("Connection refused", exception.getMessage());

        // Verify no interaction with ComponentB happens when ComponentA fails
        String resourceId = "101";
        exception = assertThrows(IOException.class, () -> {
            componentB.fetchResource(resourceId);
        });

        assertEquals("Connection refused", exception.getMessage());
    }

    @Test
    public void testInconsistentDataBetweenComponents() throws IOException, InterruptedException {
        // Test data
        String resourceData = "{\"title\":\"Test Title\",\"body\":\"Test Body\",\"userId\":1}";
        String createdResourceResponse = "{\"id\":101,\"title\":\"Test Title\",\"body\":\"Test Body\",\"userId\":1}";
        String fetchedResourceResponse = "{\"id\":101,\"title\":\"Modified Title\",\"body\":\"Modified Body\",\"userId\":1}";
        String resourceId = "101";

        // Configure responses for first and second calls
        when(mockResponse.body())
                .thenReturn(createdResourceResponse)
                .thenReturn(fetchedResourceResponse);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        // Create resource using ComponentA
        String creationResult = componentA.createResource(resourceData);

        // Fetch resource using ComponentB
        String fetchedResource = componentB.fetchResource(resourceId);

        // Verify data inconsistency detection
        boolean verificationResult = componentB.verifyResourceDetails(fetchedResource, createdResourceResponse);
        assertFalse(verificationResult);
    }

    @Test
    public void testEdgeCaseEmptyResourceData() throws IOException, InterruptedException {
        // Test with empty resource data
        String emptyResourceData = "{}";
        String createdEmptyResponse = "{\"id\":101}";

        when(mockResponse.body()).thenReturn(createdEmptyResponse);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        String creationResult = componentA.createResource(emptyResourceData);
        assertNotNull(creationResult);
        assertEquals(createdEmptyResponse, creationResult);
    }
}