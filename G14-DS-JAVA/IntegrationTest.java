
// IntegrationTest.java
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        // Initialize components
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    public void testIntegration() {
        // Test data
        Map<String, Double> items = new HashMap<>();
        items.put("Item1", 10.0);
        items.put("Item2", 20.0);

        Map<String, Integer> quantities = new HashMap<>();
        quantities.put("Item1", 2);
        quantities.put("Item2", 1);

        double discountPercentage = 10.0; // 10% discount
        double expectedFinalPrice = 36.0; // (10*2 + 20*1) * 0.9 = 36.0

        // Step 1: Calculate the total cost using Component A
        double totalCost = componentA.calculateTotalCost(items, quantities);

        // Step 2: Apply the discount using Component B
        double finalPrice = componentB.applyDiscount(totalCost, discountPercentage);

        // Step 3: Verify that the final price matches the expected price
        boolean isVerified = componentB.verifyFinalPrice(finalPrice, expectedFinalPrice);
        assertTrue(isVerified, "The final price does not match the expected price");
    }

    @Test
    public void testInvalidDiscount() {
        // Test data
        double totalCost = 100.0;
        double invalidDiscountPercentage = 110.0; // Invalid discount percentage

        // Step 1: Attempt to apply an invalid discount using Component B
        assertThrows(IllegalArgumentException.class, () -> {
            componentB.applyDiscount(totalCost, invalidDiscountPercentage);
        }, "Expected an IllegalArgumentException for invalid discount percentage");
    }
}