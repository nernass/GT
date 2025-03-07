import pytest
from component_a import component_a
from component_b import component_b

def test_component_integration():
    # Test normal flow
    result_a = component_a(5, 3)  
    final_result = component_b(result_a)
    assert final_result == 80  # (5 + 3) * 10

    # Test with zero values
    result_a = component_a(0, 0)
    final_result = component_b(result_a)
    assert final_result == 0  # (0 + 0) * 10

    # Test with negative values 
    result_a = component_a(-2, -3)
    final_result = component_b(result_a)
    assert final_result == -50  # (-2 + -3) * 10

    # Test with custom multiplier
    result_a = component_a(4, 6)
    final_result = component_b(result_a, constant=5)
    assert final_result == 50  # (4 + 6) * 5

    # Test with floating point numbers
    result_a = component_a(1.5, 2.5)
    final_result = component_b(result_a)
    assert final_result == 40  # (1.5 + 2.5) * 10