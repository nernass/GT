import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ComponentIntegrationTest {

    @Mock
    private ComponentA componentA;

    @Mock
    private ComponentB componentB;

    @InjectMocks
    private ComponentIntegrationTest componentIntegrationTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidInput() {
        // Define test data
        Map<String, Double> items = new HashMap<>();
        items.put("item1", 10.0);
        items.put("item2", 20.0);

        Map<String, Integer> quantities = new HashMap<>();
        quantities.put("item1", 2);
        quantities.put("item2", 1);

        double totalCost = 40.0;
        double discountPercentage = 10.0;
        double finalPrice = 36.0;

        // Mock ComponentA behavior
        when(componentA.calculateTotalCost(items, quantities)).thenReturn(totalCost);

        // Mock ComponentB behavior
        when(componentB.applyDiscount(totalCost, discountPercentage)).thenReturn(finalPrice);

        // Execute the workflow
        double calculatedTotalCost = componentA.calculateTotalCost(items, quantities);
        double discountedPrice = componentB.applyDiscount(calculatedTotalCost, discountPercentage);

        // Assert final output
        assertEquals(finalPrice, discountedPrice);
    }

    @Test
    public void testComponentBFailure() {
        // Define test data
        Map<String, Double> items = new HashMap<>();
        items.put("item1", 10.0);
        items.put("item2", 20.0);

        Map<String, Integer> quantities = new HashMap<>();
        quantities.put("item1", 2);
        quantities.put("item2", 1);

        double totalCost = 40.0;
        double discountPercentage = 110.0; // Invalid discount

        // Mock ComponentA behavior
        when(componentA.calculateTotalCost(items, quantities)).thenReturn(totalCost);

        // Mock ComponentB behavior to throw exception
        when(componentB.applyDiscount(totalCost, discountPercentage))
                .thenThrow(new IllegalArgumentException("Discount percentage must be between 0 and 100"));

        // Execute the workflow and assert exception
        double calculatedTotalCost = componentA.calculateTotalCost(items, quantities);
        assertThrows(IllegalArgumentException.class, () -> {
            componentB.applyDiscount(calculatedTotalCost, discountPercentage);
        });
    }

    @Test
    public void testInvalidInputToComponentA() {
        // Define test data
        Map<String, Double> items = new HashMap<>();
        Map<String, Integer> quantities = new HashMap<>();

        double totalCost = 0.0;
        double discountPercentage = 10.0;
        double finalPrice = 0.0;

        // Mock ComponentA behavior
        when(componentA.calculateTotalCost(items, quantities)).thenReturn(totalCost);

        // Mock ComponentB behavior
        when(componentB.applyDiscount(totalCost, discountPercentage)).thenReturn(finalPrice);

        // Execute the workflow
        double calculatedTotalCost = componentA.calculateTotalCost(items, quantities);
        double discountedPrice = componentB.applyDiscount(calculatedTotalCost, discountPercentage);

        // Assert final output
        assertEquals(finalPrice, discountedPrice);
    }
}