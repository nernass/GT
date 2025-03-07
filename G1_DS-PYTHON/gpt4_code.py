import pytest
from component_a import component_a
from component_b import component_b

def test_integration_component_a_b():
    # Test normal case
    num1 = 5
    num2 = 3
    result_a = component_a(num1, num2)
    result_b = component_b(result_a)
    assert result_b == (num1 + num2) * 10

    # Test with different constant
    constant = 5
    result_b = component_b(result_a, constant)
    assert result_b == (num1 + num2) * constant

    # Test with zero
    num1 = 0
    num2 = 0
    result_a = component_a(num1, num2)
    result_b = component_b(result_a)
    assert result_b == 0

    # Test with negative numbers
    num1 = -2
    num2 = -3
    result_a = component_a(num1, num2)
    result_b = component_b(result_a)
    assert result_b == (num1 + num2) * 10

    # Test with mixed positive and negative numbers
    num1 = -2
    num2 = 3
    result_a = component_a(num1, num2)
    result_b = component_b(result_a)
    assert result_b == (num1 + num2) * 10