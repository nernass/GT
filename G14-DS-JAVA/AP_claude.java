import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    private Map<String, Double> items;
    private Map<String, Integer> quantities;

    @BeforeEach
    public void setUp() {
        // Initialize components
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Set up test data
        items = new HashMap<>();
        items.put("Apple", 1.0);
        items.put("Banana", 0.5);
        items.put("Orange", 0.75);

        quantities = new HashMap<>();
        quantities.put("Apple", 5);
        quantities.put("Banana", 10);
        quantities.put("Orange", 8);
    }

    @Test
    @DisplayName("Integration test for calculation and discount - standard case")
    public void testCalculateAndApplyDiscount() {
        // Calculate total cost using ComponentA
        double totalCost = componentA.calculateTotalCost(items, quantities);

        // Expected calculation: (5*1.0) + (10*0.5) + (8*0.75) = 5 + 5 + 6 = 16
        assertEquals(16.0, totalCost, 0.001, "Total cost calculation is incorrect");

        // Apply 10% discount using ComponentB
        double discountPercentage = 10.0;
        double finalPrice = componentB.applyDiscount(totalCost, discountPercentage);

        // Expected discount: 16.0 * (1 - 10/100) = 16 * 0.9 = 14.4
        assertEquals(14.4, finalPrice, 0.001, "Discount application is incorrect");

        // Verify final price using ComponentB's verification method
        assertTrue(componentB.verifyFinalPrice(finalPrice, 14.4),
                "Final price verification failed");
    }

    @Test
    @DisplayName("Integration test with zero quantities")
    public void testWithZeroQuantities() {
        // Set all quantities to 0
        Map<String, Integer> zeroQuantities = new HashMap<>();
        zeroQuantities.put("Apple", 0);
        zeroQuantities.put("Banana", 0);
        zeroQuantities.put("Orange", 0);

        // Calculate total cost
        double totalCost = componentA.calculateTotalCost(items, zeroQuantities);
        assertEquals(0.0, totalCost, 0.001, "Total cost should be 0 with zero quantities");

        // Apply discount
        double finalPrice = componentB.applyDiscount(totalCost, 20.0);
        assertEquals(0.0, finalPrice, 0.001, "Final price should be 0");

        // Verify
        assertTrue(componentB.verifyFinalPrice(finalPrice, 0.0),
                "Zero price verification failed");
    }

    @Test
    @DisplayName("Integration test with 0% discount")
    public void testWithZeroDiscount() {
        double totalCost = componentA.calculateTotalCost(items, quantities);
        double finalPrice = componentB.applyDiscount(totalCost, 0.0);

        // No discount should be applied
        assertEquals(totalCost, finalPrice, 0.001, "Zero discount should not change price");
        assertTrue(componentB.verifyFinalPrice(finalPrice, totalCost),
                "Zero discount verification failed");
    }

    @Test
    @DisplayName("Integration test with 100% discount")
    public void testWithFullDiscount() {
        double totalCost = componentA.calculateTotalCost(items, quantities);
        double finalPrice = componentB.applyDiscount(totalCost, 100.0);

        // Full discount should make price zero
        assertEquals(0.0, finalPrice, 0.001, "Full discount should make price zero");
        assertTrue(componentB.verifyFinalPrice(finalPrice, 0.0),
                "Full discount verification failed");
    }

    @Test
    @DisplayName("Integration test with missing item in quantities map")
    public void testWithMissingItemInQuantities() {
        // Add a new item that doesn't exist in quantities map
        items.put("Grape", 2.0);

        double totalCost = componentA.calculateTotalCost(items, quantities);
        // Expected calculation: should ignore Grape price
        assertEquals(16.0, totalCost, 0.001, "Should handle missing quantities correctly");

        double finalPrice = componentB.applyDiscount(totalCost, 15.0);
        assertEquals(13.6, finalPrice, 0.001, "Discount calculation should work correctly");
    }

    @Test
    @DisplayName("Integration test error handling - invalid discount percentage")
    public void testInvalidDiscountPercentage() {
        double totalCost = componentA.calculateTotalCost(items, quantities);

        // Test with invalid discount
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            componentB.applyDiscount(totalCost, 110.0);
        });

        String expectedMessage = "Discount percentage must be between 0 and 100";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage), "Exception message doesn't match");
    }

    @ParameterizedTest
    @DisplayName("Integration test with various discount values")
    @MethodSource("provideDiscountTestData")
    public void testWithVariousDiscounts(double discountPercentage, double expectedFinalPrice) {
        double totalCost = componentA.calculateTotalCost(items, quantities);
        double finalPrice = componentB.applyDiscount(totalCost, discountPercentage);

        assertEquals(expectedFinalPrice, finalPrice, 0.001,
                "Final price calculation incorrect for " + discountPercentage + "% discount");
        assertTrue(componentB.verifyFinalPrice(finalPrice, expectedFinalPrice),
                "Final price verification failed for " + discountPercentage + "% discount");
    }

    private static Stream<Arguments> provideDiscountTestData() {
        // [discountPercentage, expectedFinalPrice]
        // The calculations assume a total cost of 16.0 from our standard test data
        return Stream.of(
                Arguments.of(5.0, 15.2), // 16 * 0.95
                Arguments.of(25.0, 12.0), // 16 * 0.75
                Arguments.of(50.0, 8.0), // 16 * 0.5
                Arguments.of(75.0, 4.0) // 16 * 0.25
        );
    }
}