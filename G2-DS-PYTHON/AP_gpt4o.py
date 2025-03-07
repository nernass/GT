import pytest
from unittest.mock import patch
from component_a import subtract_numbers
from component_b import divide_by_constant

class TestIntegration:
    @patch('component_b.divide_by_constant')
    def test_success_path(self, mock_divide_by_constant):
        # Arrange
        num1 = 10
        num2 = 2
        mock_divide_by_constant.return_value = 4.0

        # Act
        result_a = subtract_numbers(num1, num2)
        result_b = divide_by_constant(result_a)

        # Assert
        assert result_a == 8
        assert result_b == 4.0
        mock_divide_by_constant.assert_called_once_with(8, 5)

    @patch('component_b.divide_by_constant')
    def test_partial_failure(self, mock_divide_by_constant):
        # Arrange
        num1 = 10
        num2 = 2
        mock_divide_by_constant.side_effect = ValueError("Constant cannot be zero.")

        # Act
        result_a = subtract_numbers(num1, num2)
        with pytest.raises(ValueError, match="Constant cannot be zero."):
            divide_by_constant(result_a, 0)

        # Assert
        assert result_a == 8
        mock_divide_by_constant.assert_called_once_with(8, 0)

    def test_edge_case(self):
        # Arrange
        num1 = 0
        num2 = 0

        # Act
        result_a = subtract_numbers(num1, num2)
        result_b = divide_by_constant(result_a)

        # Assert
        assert result_a == 0
        assert result_b == 0.0