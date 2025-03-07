import pytest
from unittest.mock import patch
from component_a import component_a
from component_b import component_b

# Test class for integration testing
class TestIntegrationComponents:

    @patch('component_b.component_b')
    def test_success_path(self, mock_component_b):
        # Arrange
        num1, num2 = 5, 3
        result_from_a = component_a(num1, num2)
        mock_component_b.return_value = result_from_a * 10

        # Act
        result = component_b(result_from_a)

        # Assert
        assert result == 80
        mock_component_b.assert_called_once_with(result_from_a, 10)

    @patch('component_b.component_b', side_effect=Exception("Component B failed"))
    def test_partial_failure(self, mock_component_b):
        # Arrange
        num1, num2 = 5, 3
        result_from_a = component_a(num1, num2)

        # Act & Assert
        with pytest.raises(Exception, match="Component B failed"):
            component_b(result_from_a)

    def test_edge_case(self):
        # Arrange
        num1, num2 = 0, 0
        result_from_a = component_a(num1, num2)

        # Act
        result = component_b(result_from_a)

        # Assert
        assert result == 0