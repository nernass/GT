
// ComponentA.java
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ComponentA {

    private static final String API_URL = "https://jsonplaceholder.typicode.com/posts";

    /**
     * Sends a POST request to create a new resource.
     *
     * @param resourceData The data of the resource to create (in JSON format).
     * @return The ID of the created resource.
     * @throws IOException          If an I/O error occurs during the request.
     * @throws InterruptedException If the request is interrupted.
     */
    public String createResource(String resourceData) throws IOException, InterruptedException {
        // Create the HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(resourceData))
                .build();

        // Send the request and get the response
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Return the ID of the created resource (assuming the API returns the created
        // resource with an ID)
        return response.body(); // For simplicity, we return the entire response body
    }
}