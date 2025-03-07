public class ComponentA {

    /**
     * Processes a payment transaction.
     *
     * @param userId      The ID of the user making the payment.
     * @param amount      The amount to be paid.
     * @param userBalance The current balance of the user.
     * @return true if the payment is successful, false otherwise.
     */
    public boolean processPayment(String userId, double amount, double userBalance) {
        // Validate the payment amount
        if (amount <= 0) {
            System.out.println("Invalid payment amount.");
            return false;
        }

        // Check if the user has sufficient balance
        if (userBalance < amount) {
            System.out.println("Insufficient balance.");
            return false;
        }

        // Simulate payment processing
        System.out.println("Payment processed successfully for user: " + userId);
        return true;
    }
}