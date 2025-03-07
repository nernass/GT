java

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ComponentIntegrationTest {

    private ComponentA componentA;
    private ComponentB componentB;
    private Map<String, Double> items;
    private Map<String, Integer> quantities;

    @BeforeEach
    void setUp() {
        componentA = new ComponentA();
        componentB = new ComponentB();

        // Initialize test data
        items = new HashMap<>();
        items.put("apple", 1.5);
        items.put("banana", 0.75);
        items.put("orange", 2.0);

        quantities = new HashMap<>();
        quantities.put("apple", 3);
        quantities.put("banana", 5);
        quantities.put("orange", 2);
    }

    @Test
    @DisplayName("Integration test for calculating total cost and applying discount")
    void testCalculateTotalCostAndApplyDiscount() {
        // Calculate total cost using ComponentA
        double totalCost = componentA.calculateTotalCost(items, quantities);
        assertEquals(13.25, totalCost, 0.001, "Total cost calculation failed");

        // Apply discount using ComponentB
        double discountPercentage = 10.0;
        double finalPrice = componentB.applyDiscount(totalCost, discountPercentage);
        assertEquals(11.925, finalPrice, 0.001, "Discount application failed");

        // Verify price using ComponentB
        boolean isVerified = componentB.verifyFinalPrice(finalPrice, 11.925);
        assertTrue(isVerified, "Price verification failed");
    }

    @Test
    @DisplayName("Test with empty quantities")
    void testWithEmptyQuantities() {
        Map<String, Integer> emptyQuantities = new HashMap<>();
        double totalCost = componentA.calculateTotalCost(items, emptyQuantities);
        assertEquals(0.0, totalCost, 0.001, "Total cost with empty quantities should be 0");

        double finalPrice = componentB.applyDiscount(totalCost, 20.0);
        assertEquals(0.0, finalPrice, 0.001, "Final price should be 0 after discount");
    }

    @Test
    @DisplayName("Test with missing items in quantities")
    void testWithMissingItemsInQuantities() {
        Map<String, Integer> partialQuantities = new HashMap<>();
        partialQuantities.put("apple", 2);
        
        double totalCost = componentA.calculateTotalCost(items, partialQuantities);
        assertEquals(3.0, totalCost, 0.001, "Total cost calculation with partial quantities failed");
        
        double finalPrice = componentB.applyDiscount(totalCost, 15.0);
        assertEquals(2.55, finalPrice, 0.001, "Discount application failed");
    }

    @ParameterizedTest
    @CsvSource({
        "10.0, 9.0",
        "25.0, 7.5", 
        "50.0, 5.0", 
        "100.0, 0.0"
    })
    @DisplayName("Test different discount percentages")
    void testDifferentDiscountPercentages(double discountPercentage, double expected) {
        double totalCost = 10.0;
        double finalPrice = componentB.applyDiscount(totalCost, discountPercentage);
        assertEquals(expected,