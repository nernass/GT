public class ComponentA {

    /**
     * Validates user input for a registration form.
     *
     * @param username The username to validate.
     * @param email    The email to validate.
     * @param password The password to validate.
     * @return true if all inputs are valid, false otherwise.
     */
    public boolean validateInput(String username, String email, String password) {
        // Validate username (non-empty and at least 3 characters)
        if (username == null || username.length() < 3) {
            return false;
        }

        // Validate email (simple regex check)
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return false;
        }

        // Validate password (at least 8 characters)
        if (password == null || password.length() < 8) {
            return false;
        }

        return true;
    }
}