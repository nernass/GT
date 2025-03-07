// ComponentB.java
public class ComponentB {

    /**
     * Applies a discount to the total cost.
     *
     * @param totalCost          The total cost of the order.
     * @param discountPercentage The discount percentage to apply.
     * @return The final price after applying the discount.
     */
    public double applyDiscount(double totalCost, double discountPercentage) {
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        return totalCost * (1 - discountPercentage / 100);
    }

    /**
     * Verifies that the final price matches the expected price.
     *
     * @param finalPrice    The final price after applying the discount.
     * @param expectedPrice The expected price.
     * @return True if the final price matches the expected price, false otherwise.
     */
    public boolean verifyFinalPrice(double finalPrice, double expectedPrice) {
        return finalPrice == expectedPrice;
    }
}