java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComponentIntegrationTest {
    
    private ComponentA componentA;
    private ComponentB componentB;
    
    @Mock
    private HttpClient httpClient;
    
    @Mock
    private HttpResponse<String> httpResponse;
    
    private static final String TEST_RESOURCE_DATA = "{\"title\":\"test title\",\"body\":\"test body\",\"userId\":1}";
    private static final String TEST_RESOURCE_RESPONSE = "{\"id\":101,\"title\":\"test title\",\"body\":\"test body\",\"userId\":1}";
    
    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        // Initialize components
        componentA = new ComponentA();
        componentB = new ComponentB();
        
        // We need to use reflection to replace the HttpClient in both components for testing
        // This would be better handled through dependency injection in a real application
        try {
            java.lang.reflect.Field clientFieldA = ComponentA.class.getDeclaredField("client");
            clientFieldA.setAccessible(true);
            clientFieldA.set(componentA, httpClient);
            
            java.lang.reflect.Field clientFieldB = ComponentB.class.getDeclaredField("client");
            clientFieldB.setAccessible(true);
            clientFieldB.set(componentB, httpClient);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to set up test: " + e.getMessage());
        }
        
        // Mock HTTP response
        when(httpResponse.body()).thenReturn(TEST_RESOURCE_RESPONSE);
        when(httpClient.send(any(), any())).thenReturn(httpResponse);
    }
    
    @Test
    void testCreateAndFetchResourceIntegration() throws IOException, InterruptedException {
        // Create resource using ComponentA
        String createdResourceResponse = componentA.createResource(TEST_RESOURCE_DATA);
        
        // Extract the ID from the response (in a real scenario, we would parse the JSON)
        // For this test, we'll assume the ID is 101 as in the mocked response
        String resourceId = "101";
        
        // Fetch the resource using ComponentB
        String fetchedResource = componentB.fetchResource(resourceId);
        
        // Verify that the fetched resource matches the expected data
        boolean isVerified = componentB.verifyResourceDetails(fetchedResource, TEST_RESOURCE_RESPONSE);
        
        // Assertions
        assertNotNull(createdResourceResponse, "Created resource response should not be null");
        assertEquals(TEST_RESOURCE_RESPONSE, createdResourceResponse, "Created resource response should match expected");
        assertEquals(TEST_RESOURCE_RESPONSE, fetchedResource, "Fetched resource should match expected");
        assertTrue(isVerified, "Resource verification should pass");
        
        // Verify that the appropriate HTTP calls were made
        verify(httpClient, times(2)).send(any(), any());
    }
    
    @Test
    void testErrorHandlingIntegration() {
        // Test how the components handle errors
        try {
            // Configure mock to throw an exception
            when(httpClient.send(any(), any())).thenThrow(new IOException("Network error"));
            
            // Try to create a resource
            componentA.createResource(TEST_RESOURCE_DATA);
            
            fail("IOException should have been thrown");
        } catch (IOException e) {
            assertEquals("Network error", e.getMessage());
        } catch (InterruptedException e) {
            fail("Unexpected InterruptedException");
        }
    }
    
    @Test
    void testResourceVerification() throws IOException, InterruptedException {
        // Test resource verification logic
        String validResource = "{\"id\":1,\"title\":\"test\",\"body\":\"content\",\"userId\":1}";
        String invalidResource = "{\"id\":1,\"title\":\"different\",\"body\":\"content\",\"userId\":1}";
        
        // Verify with matching data
        assertTrue(componentB.verifyResourceDetails(validResource, validResource));
        
        // Verify with non-matching data
        assertFalse(componentB.verifyResourceDetails(validResource, invalidResource));
    }
}