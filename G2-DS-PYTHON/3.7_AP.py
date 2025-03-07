import pytest
from component_a import subtract_numbers
from component_b import divide_by_constant

class TestIntegration:
    """Integration tests for Component A and Component B."""
    
    def test_integration_success_path(self):
        """Test the successful flow from Component A to Component B."""
        # Define test inputs
        num1 = 20
        num2 = 5
        constant = 3
        
        # Execute Component A
        result_from_a = subtract_numbers(num1, num2)
        
        # Verify intermediate result
        assert result_from_a == 15, "Component A should subtract correctly"
        
        # Pass result to Component B
        final_result = divide_by_constant(result_from_a, constant)
        
        # Verify final result
        assert final_result == 5, "Complete workflow should calculate (20-5)/3 = 5"
    
    def test_integration_edge_cases(self):
        """Test edge cases in the integrated workflow."""
        # Test with zero result from Component A
        zero_result = subtract_numbers(10, 10)
        assert zero_result == 0
        
        final_result = divide_by_constant(zero_result, 7)
        assert final_result == 0, "Zero propagation should work correctly"
        
        # Test with negative result from Component A
        negative_result = subtract_numbers(5, 10)
        assert negative_result == -5
        
        final_result = divide_by_constant(negative_result, 2.5)
        assert final_result == -2, "Negative numbers should be handled correctly"
        
        # Test with floating point values
        float_result = subtract_numbers(10.5, 3.2)
        assert float_result == pytest.approx(7.3)
        
        final_result = divide_by_constant(float_result, 2)
        assert final_result == pytest.approx(3.65), "Floating point calculations should be accurate"
    
    def test_integration_error_handling(self):
        """Test error handling in the integrated workflow."""
        # Calculate a valid result from Component A
        result_from_a = subtract_numbers(20, 10)
        assert result_from_a == 10
        
        # Test division by zero error in Component B
        with pytest.raises(ValueError) as excinfo:
            divide_by_constant(result_from_a, 0)
        
        assert "Constant cannot be zero" in str(excinfo.value), "Should raise appropriate error message"
    
    def test_integration_default_parameters(self):
        """Test the workflow with default parameters."""
        # Calculate result from Component A
        result_from_a = subtract_numbers(25, 5)
        assert result_from_a == 20
        
        # Use default constant (5) in Component B
        final_result = divide_by_constant(result_from_a)
        
        # Verify final result
        assert final_result == 4, "Should use default constant value (5) when not specified"

    def test_integration_large_numbers(self):
        """Test the workflow with large numbers."""
        large_num1 = 1000000
        large_num2 = 500000
        
        # Component A with large numbers
        result_from_a = subtract_numbers(large_num1, large_num2)
        assert result_from_a == 500000
        
        # Component B with large input
        final_result = divide_by_constant(result_from_a, 100000)
        assert final_result == 5, "Large numbers should be handled correctly"