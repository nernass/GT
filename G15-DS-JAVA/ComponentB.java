import java.util.ArrayList;
import java.util.List;

public class ComponentB {

    // Simulate a database with an in-memory list
    private List<String[]> database = new ArrayList<>();

    /**
     * Saves validated user data to the database.
     *
     * @param username The validated username.
     * @param email    The validated email.
     * @param password The validated password.
     * @return true if the data was saved successfully, false otherwise.
     */
    public boolean saveToDatabase(String username, String email, String password) {
        if (username == null || email == null || password == null) {
            return false;
        }

        // Save the data to the "database"
        database.add(new String[] { username, email, password });
        return true;
    }

    /**
     * Retrieves the last entry from the database for testing purposes.
     *
     * @return The last entry as a String array [username, email, password].
     */
    public String[] getLastEntry() {
        if (database.isEmpty()) {
            return null;
        }
        return database.get(database.size() - 1);
    }
}