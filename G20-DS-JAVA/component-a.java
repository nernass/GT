import java.util.UUID;

public class ComponentA {

    /**
     * Generates a unique ID for a new resource.
     *
     * @return A unique ID as a string.
     */
    public String generateUniqueId() {
        // Generate a UUID (Universally Unique Identifier)
        String uniqueId = UUID.randomUUID().toString();
        System.out.println("Generated unique ID: " + uniqueId);
        return uniqueId;
    }
}