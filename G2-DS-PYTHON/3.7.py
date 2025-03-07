import pytest
import component_a
import component_b

class TestIntegration:
    def test_normal_flow(self):
        """Test the normal integration path of ComponentA and ComponentB."""
        # Step 1: Use Component A
        a_result = component_a.subtract_numbers(10, 4)
        assert a_result == 6
        
        # Step 2: Pass result to Component B
        b_result = component_b.divide_by_constant(a_result)
        assert b_result == 1.2
    
    def test_integration_with_custom_constant(self):
        """Test the integration with a custom constant value."""
        # Use ComponentA and pass result to ComponentB with custom constant
        a_result = component_a.subtract_numbers(20, 10)
        b_result = component_b.divide_by_constant(a_result, constant=2)
        assert b_result == 5.0
    
    def test_integration_with_negative_numbers(self):
        """Test integration with negative numbers."""
        # Component A with negative numbers
        a_result = component_a.subtract_numbers(-5, 5)
        assert a_result == -10
        
        # Pass negative result to Component B
        b_result = component_b.divide_by_constant(a_result, constant=2)
        assert b_result == -5.0
    
    def test_integration_with_zero_result_from_a(self):
        """Test integration when ComponentA returns zero."""
        # Component A returns zero
        a_result = component_a.subtract_numbers(5, 5)
        assert a_result == 0
        
        # Pass zero to Component B
        b_result = component_b.divide_by_constant(a_result)
        assert b_result == 0
    
    def test_error_propagation(self):
        """Test error handling when ComponentB receives invalid constant."""
        # Component A operation
        a_result = component_a.subtract_numbers(15, 5)
        
        # Component B should raise ValueError with constant=0
        with pytest.raises(ValueError, match="Constant cannot be zero."):
            component_b.divide_by_constant(a_result, constant=0)
    
    def test_float_handling(self):
        """Test integration with floating point numbers."""
        # Component A with floats
        a_result = component_a.subtract_numbers(10.5, 2.5)
        assert a_result == 8.0
        
        # Pass float to Component B
        b_result = component_b.divide_by_constant(a_result, constant=4)
        assert b_result == 2.0