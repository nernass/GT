public class ComponentB {

    /**
     * Updates the user's balance after a payment.
     *
     * @param userId      The ID of the user.
     * @param amount      The amount to deduct from the balance.
     * @param userBalance The current balance of the user.
     * @return The updated balance.
     */
    public double updateBalance(String userId, double amount, double userBalance) {
        if (amount <= 0 || userBalance < amount) {
            throw new IllegalArgumentException("Invalid transaction: Insufficient balance or invalid amount.");
        }

        // Deduct the payment amount from the balance
        double updatedBalance = userBalance - amount;
        System.out.println("Balance updated for user: " + userId + ". New balance: " + updatedBalance);
        return updatedBalance;
    }

    /**
     * Verifies the transaction by checking if the balance was updated correctly.
     *
     * @param userId     The ID of the user.
     * @param amount     The amount deducted.
     * @param oldBalance The balance before the transaction.
     * @param newBalance The balance after the transaction.
     * @return true if the transaction is verified, false otherwise.
     */
    public boolean verifyTransaction(String userId, double amount, double oldBalance, double newBalance) {
        if (newBalance != (oldBalance - amount)) {
            System.out.println("Transaction verification failed for user: " + userId);
            return false;
        }
        System.out.println("Transaction verified successfully for user: " + userId);
        return true;
    }
}