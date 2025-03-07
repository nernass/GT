import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ComponentIntegrationTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    public void testSuccessPath() throws IOException, InterruptedException {
        // Mock the HTTP response for ComponentA
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);
        when(mockHttpResponse.body()).thenReturn("{ \"id\": \"1\" }");

        // Execute ComponentA to create a resource
        String resourceData = "{ \"title\": \"foo\", \"body\": \"bar\", \"userId\": 1 }";
        String createdResourceId = componentA.createResource(resourceData);

        // Mock the HTTP response for ComponentB
        when(mockHttpResponse.body())
                .thenReturn("{ \"id\": \"1\", \"title\": \"foo\", \"body\": \"bar\", \"userId\": 1 }");

        // Execute ComponentB to fetch the resource
        String fetchedResource = componentB.fetchResource(createdResourceId);

        // Verify the fetched resource matches the expected data
        assertTrue(componentB.verifyResourceDetails(fetchedResource,
                "{ \"id\": \"1\", \"title\": \"foo\", \"body\": \"bar\", \"userId\": 1 }"));
    }

    @Test
    public void testComponentBFailure() throws IOException, InterruptedException {
        // Mock the HTTP response for ComponentA
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);
        when(mockHttpResponse.body()).thenReturn("{ \"id\": \"1\" }");

        // Execute ComponentA to create a resource
        String resourceData = "{ \"title\": \"foo\", \"body\": \"bar\", \"userId\": 1 }";
        String createdResourceId = componentA.createResource(resourceData);

        // Mock ComponentB to throw an exception
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Failed to fetch resource"));

        // Verify that an exception is thrown when fetching the resource
        assertThrows(IOException.class, () -> {
            componentB.fetchResource(createdResourceId);
        });
    }

    @Test
    public void testInvalidInputToComponentA() throws IOException, InterruptedException {
        // Mock the HTTP response for ComponentA to return an error
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);
        when(mockHttpResponse.body()).thenReturn("{ \"error\": \"Invalid input\" }");

        // Execute ComponentA with invalid input
        String invalidResourceData = "{ \"invalid\": \"data\" }";
        String response = componentA.createResource(invalidResourceData);

        // Verify that the response contains the error message
        assertEquals("{ \"error\": \"Invalid input\" }", response);
    }
}