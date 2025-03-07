import pytest
from component_a import subtract_numbers
from component_b import divide_by_constant

def test_components_integration_positive_numbers():
    # First test with positive numbers
    intermediate_result = subtract_numbers(10, 4)
    final_result = divide_by_constant(intermediate_result)
    assert final_result == 1.2

def test_components_integration_negative_numbers():
    # Test with negative numbers
    intermediate_result = subtract_numbers(-8, -15)
    final_result = divide_by_constant(intermediate_result)
    assert final_result == 1.4

def test_components_integration_zero_result():
    # Test when subtraction results in zero
    intermediate_result = subtract_numbers(5, 5)
    final_result = divide_by_constant(intermediate_result)
    assert final_result == 0

def test_components_integration_custom_constant():
    # Test with a custom constant value
    intermediate_result = subtract_numbers(20, 10)
    final_result = divide_by_constant(intermediate_result, constant=2)
    assert final_result == 5

def test_components_integration_division_by_zero():
    # Test division by zero error
    intermediate_result = subtract_numbers(10, 5)
    with pytest.raises(ValueError):
        divide_by_constant(intermediate_result, constant=0)

def test_components_integration_float_values():
    # Test with floating point numbers
    intermediate_result = subtract_numbers(5.5, 2.2)
    final_result = divide_by_constant(intermediate_result)
    assert round(final_result, 2) == 0.66