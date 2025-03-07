
// ComponentB.java
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ComponentB {

    private static final String API_URL = "https://jsonplaceholder.typicode.com/posts";

    /**
     * Sends a GET request to fetch a resource by ID.
     *
     * @param resourceId The ID of the resource to fetch.
     * @return The details of the resource (in JSON format).
     * @throws IOException          If an I/O error occurs during the request.
     * @throws InterruptedException If the request is interrupted.
     */
    public String fetchResource(String resourceId) throws IOException, InterruptedException {
        // Create the HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/" + resourceId))
                .GET()
                .build();

        // Send the request and get the response
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Return the response body
        return response.body();
    }

    /**
     * Verifies that the fetched resource matches the expected data.
     *
     * @param fetchedResource The fetched resource (in JSON format).
     * @param expectedData    The expected data (in JSON format).
     * @return True if the fetched resource matches the expected data, false
     *         otherwise.
     */
    public boolean verifyResourceDetails(String fetchedResource, String expectedData) {
        // For simplicity, we compare the JSON strings directly
        return fetchedResource.equals(expectedData);
    }
}