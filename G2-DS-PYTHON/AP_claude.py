import pytest
from component_a import subtract_numbers
from component_b import divide_by_constant

class TestIntegration:
    """Integration tests for component_a and component_b."""
    
    def test_successful_integration(self):
        """Test the successful flow from subtract_numbers to divide_by_constant."""
        # Component A processing
        subtraction_result = subtract_numbers(10, 5)
        # Passing result to Component B
        final_result = divide_by_constant(subtraction_result)
        # Assert the end-to-end result
        assert final_result == 1.0, "Integration failed: (10-5)/5 should equal 1.0"
    
    def test_integration_with_custom_constant(self):
        """Test integration with a non-default constant value."""
        # Component A processing
        subtraction_result = subtract_numbers(20, 10)
        # Passing result to Component B with custom constant
        final_result = divide_by_constant(subtraction_result, constant=2)
        # Assert the end-to-end result
        assert final_result == 5.0, "Integration failed: (20-10)/2 should equal 5.0"
    
    def test_integration_with_zero_constant_error(self):
        """Test error handling when component_b receives an invalid constant."""
        # Component A processing
        subtraction_result = subtract_numbers(15, 5)
        # Verify error propagation when Component B fails
        with pytest.raises(ValueError, match="Constant cannot be zero."):
            divide_by_constant(subtraction_result, constant=0)
    
    def test_integration_with_negative_numbers(self):
        """Test integration with negative numbers."""
        # Component A with negative inputs
        subtraction_result = subtract_numbers(-10, 5)
        # Passing negative result to Component B
        final_result = divide_by_constant(subtraction_result)
        # Assert the end-to-end result
        assert final_result == -3.0, "Integration failed: (-10-5)/5 should equal -3.0"
    
    def test_integration_with_zero_result(self):
        """Test integration when component_a produces zero."""
        # Component A produces zero
        subtraction_result = subtract_numbers(5, 5)
        # Passing zero to Component B
        final_result = divide_by_constant(subtraction_result)
        # Assert the end-to-end result
        assert final_result == 0.0, "Integration failed: (5-5)/5 should equal 0.0"
    
    def test_integration_with_float_inputs(self):
        """Test integration with floating-point numbers."""
        # Component A with float inputs
        subtraction_result = subtract_numbers(10.5, 2.5)
        # Passing float result to Component B
        final_result = divide_by_constant(subtraction_result)
        # Assert the end-to-end result
        assert final_result == 1.6, "Integration failed: (10.5-2.5)/5 should equal 1.6"
    
    def test_integration_with_large_numbers(self):
        """Test integration with large numbers."""
        # Component A with large inputs
        subtraction_result = subtract_numbers(1000000, 500000)
        # Passing large result to Component B
        final_result = divide_by_constant(subtraction_result)
        # Assert the end-to-end result
        assert final_result == 100000.0, "Integration failed: (1000000-500000)/5 should equal 100000.0"