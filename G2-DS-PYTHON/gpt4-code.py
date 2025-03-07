import pytest
from component_a import subtract_numbers
from component_b import divide_by_constant

def test_integration_subtract_and_divide():
    # Test normal flow
    result_a = subtract_numbers(10, 3)
    result_b = divide_by_constant(result_a, 2)
    assert result_b == 3.5

    # Test division by default constant
    result_a = subtract_numbers(15, 5)
    result_b = divide_by_constant(result_a)
    assert result_b == 2.0

    # Test error handling for division by zero
    result_a = subtract_numbers(20, 5)
    with pytest.raises(ValueError, match="Constant cannot be zero."):
        divide_by_constant(result_a, 0)