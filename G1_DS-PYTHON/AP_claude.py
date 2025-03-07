import pytest
from unittest.mock import patch, MagicMock
from component_a import component_a
from component_b import component_b

class TestIntegration:
    
    def test_success_path(self):
        """Test the successful flow from component_a to component_b"""
        # Test normal inputs
        num1, num2 = 5, 7
        constant = 10
        
        # Execute the workflow
        result_a = component_a(num1, num2)
        final_result = component_b(result_a, constant)
        
        # Assert the final output matches expectations
        assert result_a == 12
        assert final_result == 120
        
    def test_edge_cases(self):
        """Test boundary values across the components"""
        # Test zero values
        assert component_b(component_a(0, 0)) == 0
        
        # Test negative values
        assert component_b(component_a(-5, -7)) == -120
        
        # Test with different constant
        assert component_b(component_a(5, 5), 2) == 20
        
        # Test large numbers
        large_num = 10**6
        expected = (large_num + large_num) * 10
        assert component_b(component_a(large_num, large_num)) == expected
    
    @patch('component_a.component_a')
    def test_component_a_failure(self, mock_component_a):
        """Test how component_b handles when component_a fails"""
        # Setup component_a to raise an exception
        mock_component_a.side_effect = ValueError("Simulated failure in component_a")
        
        # Test that the error propagates
        with pytest.raises(ValueError) as excinfo:
            result_a = mock_component_a(5, 7)
            component_b(result_a)
            
        assert "Simulated failure in component_a" in str(excinfo.value)
    
    @patch('component_b.component_b')
    def test_component_b_failure(self, mock_component_b):
        """Test how errors in component_b are handled"""
        # Setup component_b to raise an exception
        mock_component_b.side_effect = ValueError("Simulated failure in component_b")
        
        # Execute the workflow with error in component_b
        result_a = component_a(5, 7)
        
        # Test that the error propagates
        with pytest.raises(ValueError) as excinfo:
            component_b(result_a)
            
        assert "Simulated failure in component_b" in str(excinfo.value)
    
    def test_data_flow_accuracy(self):
        """Test that data flows correctly between components"""
        with patch('component_b.component_b') as mock_component_b:
            # Set up the mock to track calls
            mock_component_b.return_value = 120
            
            # Execute the workflow
            result_a = component_a(5, 7)
            final_result = mock_component_b(result_a)
            
            # Verify component_b receives the correct input from component_a
            mock_component_b.assert_called_once_with(12, 10)
            assert final_result == 120