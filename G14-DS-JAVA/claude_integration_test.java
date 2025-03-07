import org.junit.jupiter.api.*;
import java.util.HashMap;
import java.util.Map;

public class IntegrationTest {
    private ComponentA componentA;
    private ComponentB componentB;
    private Map<String, Double> items;
    private Map<String, Integer> quantities;

    @BeforeEach
    void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();
        items = new HashMap<>();
        quantities = new HashMap<>();
    }

    @Test
    void testOrderProcessingWithDiscount() {
        // Setup test data
        items.put("item1", 100.0);
        items.put("item2", 50.0);
        quantities.put("item1", 2);
        quantities.put("item2", 1);

        // Calculate total cost using ComponentA
        double totalCost = componentA.calculateTotalCost(items, quantities);
        Assertions.assertEquals(250.0, totalCost);

        // Apply discount using ComponentB
        double discountPercentage = 20.0;
        double finalPrice = componentB.applyDiscount(totalCost, discountPercentage);
        Assertions.assertEquals(200.0, finalPrice);

        // Verify final price
        boolean isCorrect = componentB.verifyFinalPrice(finalPrice, 200.0);
        Assertions.assertTrue(isCorrect);
    }

    @Test
    void testEmptyOrder() {
        double totalCost = componentA.calculateTotalCost(items, quantities);
        Assertions.assertEquals(0.0, totalCost);

        double finalPrice = componentB.applyDiscount(totalCost, 10.0);
        Assertions.assertEquals(0.0, finalPrice);
    }

    @Test
    void testInvalidDiscount() {
        items.put("item1", 100.0);
        quantities.put("item1", 1);
        double totalCost = componentA.calculateTotalCost(items, quantities);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            componentB.applyDiscount(totalCost, -10.0);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            componentB.applyDiscount(totalCost, 110.0);
        });
    }

    @Test
    void testMismatchedItemsAndQuantities() {
        items.put("item1", 100.0);
        items.put("item2", 50.0);
        quantities.put("item1", 1);
        // item2 quantity not specified

        double totalCost = componentA.calculateTotalCost(items, quantities);
        Assertions.assertEquals(100.0, totalCost);

        double finalPrice = componentB.applyDiscount(totalCost, 50.0);
        Assertions.assertEquals(50.0, finalPrice);
    }
}