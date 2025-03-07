import pytest
from component_a import component_a
from component_b import component_b

def test_integration_normal_flow():
    """Test the normal flow of data from component_a to component_b"""
    # Test with positive integers
    result_a = component_a(5, 7)
    assert result_a == 12
    result_b = component_b(result_a)
    assert result_b == 120
    
    # Test with negative integers
    result_a = component_a(-3, -4)
    assert result_a == -7
    result_b = component_b(result_a)
    assert result_b == -70
    
    # Test with zero values
    result_a = component_a(0, 0)
    assert result_a == 0
    result_b = component_b(result_a)
    assert result_b == 0

def test_integration_custom_constant():
    """Test integration with a custom constant value in component_b"""
    result_a = component_a(10, 20)
    assert result_a == 30
    result_b = component_b(result_a, constant=5)
    assert result_b == 150

def test_integration_float_values():
    """Test integration with floating point values"""
    result_a = component_a(3.5, 2.5)
    assert result_a == 6.0
    result_b = component_b(result_a)
    assert result_b == 60.0

def test_integration_end_to_end():
    """Test end-to-end pipeline in a single function call"""
    # Direct integration without storing intermediate results
    assert component_b(component_a(5, 5)) == 100
    assert component_b(component_a(-5, 5), constant=5) == 0
    assert component_b(component_a(0.1, 0.2), constant=100) == 30.0

def test_integration_type_handling():
    """Test type handling when passing data between components"""
    # Test with different numeric types
    result_a = component_a(5, 5.5)
    assert isinstance(result_a, float)
    result_b = component_b(result_a)
    assert isinstance(result_b, float)