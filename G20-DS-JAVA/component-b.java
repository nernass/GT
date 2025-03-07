import java.util.HashSet;
import java.util.Set;

public class ComponentB {

    // Simulate a database of saved resource IDs
    private Set<String> savedResourceIds = new HashSet<>();

    /**
     * Saves the resource with the generated ID and verifies its uniqueness.
     *
     * @param resourceId The unique ID of the resource.
     * @return true if the ID is unique and the resource is saved, false otherwise.
     */
    public boolean saveResource(String resourceId) {
        if (resourceId == null || resourceId.isEmpty()) {
            throw new IllegalArgumentException("Resource ID must not be null or empty.");
        }

        // Check if the ID already exists
        if (savedResourceIds.contains(resourceId)) {
            System.out.println("Resource ID is not unique: " + resourceId);
            return false;
        }

        // Save the resource ID
        savedResourceIds.add(resourceId);
        System.out.println("Resource saved with ID: " + resourceId);
        return true;
    }

    /**
     * Retrieves the set of saved resource IDs for testing purposes.
     *
     * @return The set of saved resource IDs.
     */
    public Set<String> getSavedResourceIds() {
        return savedResourceIds;
    }
}