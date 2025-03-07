IntegrationTest.java
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComponentIntegrationTest {
    
    private MockedStatic<HttpClient> httpClientMock;
    private HttpClient mockClient;
    private HttpResponse<String> mockResponse;
    private ComponentA componentA;
    private ComponentB componentB;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    public void setUp() {
        // Initialize components
        componentA = new ComponentA();
        componentB = new ComponentB();
        objectMapper = new ObjectMapper();
        
        // Setup mocks
        mockClient = mock(HttpClient.class);
        mockResponse = mock(HttpResponse.class);
        httpClientMock = Mockito.mockStatic(HttpClient.class);
        httpClientMock.when(HttpClient::newHttpClient).thenReturn(mockClient);
    }
    
    @AfterEach
    public void tearDown() {
        // Close the static mock to prevent memory leaks
        httpClientMock.close();
    }
    
    @Test
    public void testSuccessfulResourceCreationAndRetrieval() throws IOException, InterruptedException {
        // Test data
        String resourceData = "{\"title\":\"test title\",\"body\":\"test body\",\"userId\":1}";
        String createdResourceResponse = "{\"id\":101,\"title\":\"test title\",\"body\":\"test body\",\"userId\":1}";
        
        // Configure mocks for resource creation and retrieval
        when(mockClient.send(any(), any())).thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(createdResourceResponse);
        
        // Step 1: Create resource using ComponentA
        String createdResource = componentA.createResource(resourceData);
        
        // Step 2: Parse the response to extract the ID
        JsonNode jsonNode = objectMapper.readTree(createdResource);
        String resourceId = jsonNode.get("id").asText();
        
        // Step 3: Fetch the resource using ComponentB
        String fetchedResource = componentB.fetchResource(resourceId);
        
        // Step 4: Verify the resource details
        boolean isValid = componentB.verifyResourceDetails(fetchedResource, createdResourceResponse);
        
        // Assertions
        assertTrue(isValid, "The resource details should match the expected data");
        assertEquals("101", resourceId, "The resource ID should be extracted correctly");
        
        // Verify the HTTP client was called correctly for both components
        verify(mockClient, times(2)).send(any(), any());
    }
    
    @Test
    public void testResourceCreationSuccessButFetchFailure() throws IOException, InterruptedException {
        // Test data
        String resourceData = "{\"title\":\"test title\",\"body\":\"test body\",\"userId\":1}";
        String createdResourceResponse = "{\"id\":101,\"title\":\"test title\",\"body\":\"test body\",\"userId\":1}";
        
        // Configure mock for resource creation (success) and fetch (failure)
        when(mockClient.send(any(), any()))
            .thenReturn(mockResponse)
            .thenThrow(new IOException("Network error during fetch"));
        when(mockResponse.body()).thenReturn(createdResourceResponse);
        
        // Step 1: Create resource using ComponentA
        String createdResource = componentA.createResource(resourceData);
        
        // Step 2: Parse the response to extract the ID
        JsonNode jsonNode = objectMapper.readTree(createdResource);
        String resourceId = jsonNode.get("id").asText();
        
        // Step 3: Try to fetch the resource - should fail
        assertThrows(IOException.class, () -> {
            componentB.fetchResource(resourceId);
        }, "Fetch should fail with IOException");
        
        // Verify interactions
        verify(mockClient, times(2)).send(any(), any());
    }
    
    @Test
    public void testResourceVerificationWithMismatchedData() throws IOException, InterruptedException {
        // Test data
        String resourceData = "{\"title\":\"test title\",\"body\":\"test body\",\"userId\":1}";
        String createdResourceResponse = "{\"id\":101,\"title\":\"test title\",\"body\":\"test body\",\"userId\":1}";
        String fetchedResourceResponse = "{\"id\":101,\"title\":\"different title\",\"body\":\"different body\",\"userId\":1}";
        
        // Configure mocks for different responses on each call
        when(mockClient.send(any(), any())).thenReturn(mockResponse);
        when(mockResponse.body())
            .thenReturn(createdResourceResponse) // First call (create)
            .thenReturn(fetchedResourceResponse); // Second call (fetch)
        
        // Step 1: Create resource using ComponentA
        String createdResource = componentA.createResource(resourceData);
        
        // Step 2: Parse the response to extract the ID
        JsonNode jsonNode = objectMapper.readTree(createdResource);
        String resourceId = jsonNode.get("id").asText();
        
        // Step 3: Fetch the resource using ComponentB
        String fetchedResource = componentB.fetchResource(resourceId);
        
        // Step 4: Verify the resource details (should fail as data is different)
        boolean isValid = componentB.verifyResourceDetails(fetchedResource, createdResourceResponse);
        
        // Assertions
        assertFalse(isValid, "The verification should fail due to mismatched data");
        
        // Verify interactions
        verify(mockClient, times(2)).send(any(), any());
    }
    
    @Test
    public void testResourceCreationFailure() throws IOException, InterruptedException {
        // Test data
        String resourceData = "{\"title\":\"test title\",\"body\":\"test body\",\"userId\":1}";
        
        // Configure mock to throw exception during creation
        when(mockClient.send(any(), any())).thenThrow(new IOException("Network error during creation"));
        
        // Try to create resource using ComponentA - should fail
        assertThrows(IOException.class, () -> {
            componentA.createResource(resourceData);
        }, "Resource creation should fail with IOException");
        
        // Since creation failed, we shouldn't proceed to ComponentB
        // Verify the HTTP client was called only once
        verify(mockClient, times(1)).send(any(), any());
    }
}