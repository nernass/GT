
// ComponentA.java
import java.util.Map;

public class ComponentA {

    /**
     * Calculates the total cost of an order.
     *
     * @param items      A map of item names to their prices.
     * @param quantities A map of item names to their quantities.
     * @return The total cost of the order.
     */
    public double calculateTotalCost(Map<String, Double> items, Map<String, Integer> quantities) {
        double totalCost = 0.0;

        // Calculate the total cost by multiplying each item's price by its quantity
        for (Map.Entry<String, Double> entry : items.entrySet()) {
            String itemName = entry.getKey();
            double itemPrice = entry.getValue();
            int itemQuantity = quantities.getOrDefault(itemName, 0);
            totalCost += itemPrice * itemQuantity;
        }

        return totalCost;
    }
}