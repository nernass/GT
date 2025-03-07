import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;

    @BeforeEach
    public void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
    }

    @Test
    public void testCalculateTotalCostAndApplyDiscount() {
        // Setup items and quantities
        Map<String, Double> items = new HashMap<>();
        items.put("item1", 10.0);
        items.put("item2", 20.0);

        Map<String, Integer> quantities = new HashMap<>();
        quantities.put("item1", 2);
        quantities.put("item2", 1);

        // Calculate total cost
        double totalCost = componentA.calculateTotalCost(items, quantities);
        assertEquals(40.0, totalCost, 0.01);

        // Apply discount
        double discountPercentage = 10.0;
        double finalPrice = componentB.applyDiscount(totalCost, discountPercentage);
        assertEquals(36.0, finalPrice, 0.01);

        // Verify final price
        boolean isPriceCorrect = componentB.verifyFinalPrice(finalPrice, 36.0);
        assertTrue(isPriceCorrect);
    }

    @Test
    public void testApplyDiscountWithInvalidPercentage() {
        // Setup items and quantities
        Map<String, Double> items = new HashMap<>();
        items.put("item1", 10.0);
        items.put("item2", 20.0);

        Map<String, Integer> quantities = new HashMap<>();
        quantities.put("item1", 2);
        quantities.put("item2", 1);

        // Calculate total cost
        double totalCost = componentA.calculateTotalCost(items, quantities);
        assertEquals(40.0, totalCost, 0.01);

        // Apply invalid discount
        double invalidDiscountPercentage = 110.0;
        assertThrows(IllegalArgumentException.class, () -> {
            componentB.applyDiscount(totalCost, invalidDiscountPercentage);
        });
    }
}