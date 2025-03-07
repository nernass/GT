import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Integration tests for verifying interactions between ComponentA and
 * ComponentB.
 * Tests the workflow of calculating order total cost and applying discounts.
 */
public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    private Map<String, Double> itemPrices;
    private Map<String, Integer> itemQuantities;

    @BeforeEach
    public void setUp() {
        // Initialize components
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Initialize test data
        itemPrices = new HashMap<>();
        itemQuantities = new HashMap<>();
    }

    /**
     * Scenario 1: Valid input with discount
     * Tests the complete flow:
     * 1. Calculate total cost using ComponentA
     * 2. Apply discount using ComponentB
     * 3. Verify final price matches expected value
     */
    @Test
    public void testOrderProcessingWithDiscount() {
        // Setup test data
        itemPrices.put("Apple", 1.50);
        itemPrices.put("Banana", 0.75);
        itemPrices.put("Orange", 1.25);

        itemQuantities.put("Apple", 3);
        itemQuantities.put("Banana", 2);
        itemQuantities.put("Orange", 4);

        // Calculate total cost using ComponentA
        double totalCost = componentA.calculateTotalCost(itemPrices, itemQuantities);

        // Calculate expected total: (3*1.5) + (2*0.75) + (4*1.25) = 11.0
        double expectedTotalCost = 11.0;
        assertEquals(expectedTotalCost, totalCost, 0.001, "Total cost calculation incorrect");

        // Apply 10% discount using ComponentB
        double discountPercentage = 10.0;
        double finalPrice = componentB.applyDiscount(totalCost, discountPercentage);

        // Expected final price after 10% discount: 11.0 * (1 - 10/100) = 9.9
        double expectedFinalPrice = 9.9;
        assertEquals(expectedFinalPrice, finalPrice, 0.001, "Final price after discount incorrect");

        // Verify final price using ComponentB's verification method
        boolean isPriceCorrect = componentB.verifyFinalPrice(finalPrice, expectedFinalPrice);
        assertTrue(isPriceCorrect, "Price verification failed");
    }

    /**
     * Scenario 2: Edge case - Empty order
     * Tests handling of an order with no items
     */
    @Test
    public void testEmptyOrder() {
        // Calculate total cost of empty order
        double totalCost = componentA.calculateTotalCost(itemPrices, itemQuantities);
        assertEquals(0.0, totalCost, "Empty order should have zero cost");

        // Apply discount to zero cost
        double discountPercentage = 20.0;
        double finalPrice = componentB.applyDiscount(totalCost, discountPercentage);
        assertEquals(0.0, finalPrice, "Discounted price of zero should remain zero");
    }

    /**
     * Scenario 3: Edge case - Maximum allowed discount
     * Tests applying 100% discount (boundary value)
     */
    @Test
    public void testMaximumDiscount() {
        // Setup test data
        itemPrices.put("Product", 100.0);
        itemQuantities.put("Product", 1);

        // Calculate total cost
        double totalCost = componentA.calculateTotalCost(itemPrices, itemQuantities);
        assertEquals(100.0, totalCost, "Total cost calculation incorrect");

        // Apply 100% discount (boundary value)
        double discountPercentage = 100.0;
        double finalPrice = componentB.applyDiscount(totalCost, discountPercentage);
        assertEquals(0.0, finalPrice, "100% discount should result in zero price");
    }

    /**
     * Scenario 4: Invalid discount percentage
     * Tests error handling when providing invalid discount
     */
    @Test
    public void testInvalidDiscountPercentage() {
        // Setup test data
        itemPrices.put("Product", 50.0);
        itemQuantities.put("Product", 2);

        // Calculate total cost
        double totalCost = componentA.calculateTotalCost(itemPrices, itemQuantities);

        // Test with invalid discount (negative)
        assertThrows(IllegalArgumentException.class, () -> {
            componentB.applyDiscount(totalCost, -10.0);
        }, "Negative discount percentage should throw IllegalArgumentException");

        // Test with invalid discount (over 100%)
        assertThrows(IllegalArgumentException.class, () -> {
            componentB.applyDiscount(totalCost, 150.0);
        }, "Discount percentage over 100 should throw IllegalArgumentException");
    }

    /**
     * Scenario 5: Item missing from quantities map
     * Tests handling of items that exist in prices but not quantities
     */
    @Test
    public void testMissingQuantity() {
        // Setup test data - price exists but quantity doesn't
        itemPrices.put("Apple", 1.50);
        itemPrices.put("Banana", 0.75);

        // Only set quantity for Apple
        itemQuantities.put("Apple", 2);

        // Calculate total cost - should use 0 for Banana's quantity
        double totalCost = componentA.calculateTotalCost(itemPrices, itemQuantities);
        assertEquals(3.0, totalCost, "Should only count items with quantities");

        // Apply discount
        double finalPrice = componentB.applyDiscount(totalCost, 20.0);
        assertEquals(2.4, finalPrice, 0.001, "Discount calculation incorrect");
    }
}