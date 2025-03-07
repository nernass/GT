import unittest
from unittest.mock import patch
import pytest

# Import the components
from component_a import component_a
from component_b import component_b

class TestComponentIntegration(unittest.TestCase):
    
    def setUp(self):
        # Setup could include any shared test data
        self.valid_input_1 = 5
        self.valid_input_2 = 10
        self.expected_result_a = 15  # 5 + 10
        self.default_multiplier = 10
        self.expected_final_result = 150  # (5 + 10) * 10
        
        # Edge case values
        self.zero_value = 0
        self.negative_value = -5
        self.large_value = 10**6
    
    def test_integration_success_path(self):
        """Test the successful integration between component_a and component_b"""
        # Execute component A
        result_a = component_a(self.valid_input_1, self.valid_input_2)
        self.assertEqual(result_a, self.expected_result_a)
        
        # Pass result from A to component B
        final_result = component_b(result_a)
        self.assertEqual(final_result, self.expected_final_result)
        
        # Test the end-to-end flow in a single call
        end_to_end_result = component_b(component_a(self.valid_input_1, self.valid_input_2))
        self.assertEqual(end_to_end_result, self.expected_final_result)
    
    def test_integration_with_custom_constant(self):
        """Test integration with a custom multiplier in component_b"""
        custom_multiplier = 5
        
        # Execute the workflow with custom constant
        result_a = component_a(self.valid_input_1, self.valid_input_2)
        final_result = component_b(result_a, constant=custom_multiplier)
        
        # Verify result
        self.assertEqual(final_result, self.expected_result_a * custom_multiplier)
    
    def test_integration_with_edge_cases(self):
        """Test integration with edge case inputs"""
        
        # Test with zero
        zero_sum = component_a(self.zero_value, self.zero_value)
        zero_result = component_b(zero_sum)
        self.assertEqual(zero_result, 0)
        
        # Test with negative numbers
        neg_sum = component_a(self.negative_value, self.negative_value)
        neg_result = component_b(neg_sum)
        self.assertEqual(neg_result, -10 * self.default_multiplier)
        
        # Test with mixed numbers (negative + positive)
        mixed_sum = component_a(self.negative_value, self.valid_input_1)
        mixed_result = component_b(mixed_sum)
        self.assertEqual(mixed_result, 0 * self.default_multiplier)  # -5 + 5 = 0, 0 * 10 = 0
        
        # Test with large numbers
        large_sum = component_a(self.large_value, self.large_value)
        large_result = component_b(large_sum)
        self.assertEqual(large_result, 2 * self.large_value * self.default_multiplier)
    
    @patch('component_a.component_a')
    def test_component_interaction_with_mock(self, mock_component_a):
        """Test component interaction using mocks to verify correct parameters are passed"""
        # Setup mock
        mock_component_a.return_value = 42
        
        # Call the end-to-end flow
        result = component_b(mock_component_a(self.valid_input_1, self.valid_input_2))
        
        # Verify component_a was called with correct parameters
        mock_component_a.assert_called_once_with(self.valid_input_1, self.valid_input_2)
        
        # Verify final result
        self.assertEqual(result, 42 * self.default_multiplier)

if __name__ == '__main__':
    unittest.main()