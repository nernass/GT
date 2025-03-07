# test_integration.py
import unittest
from component_a import subtract_numbers
from component_b import divide_by_constant

class TestIntegration(unittest.TestCase):
    """
    Integration test to verify the interaction between Component A and Component B.
    """
    def test_integration(self):
        """
        Tests the integration of Component A and Component B.
        """
        # Test input values
        num1, num2 = 20, 10

        # Step 1: Call Component A to subtract num2 from num1
        result_a = subtract_numbers(num1, num2)

        # Step 2: Call Component B to divide the result from Component A by 5
        result_b = divide_by_constant(result_a, constant=5)

        # Verify the final result
        expected_result = 2  # (20 - 10) / 5 = 2
        self.assertEqual(result_b, expected_result, f"Expected {expected_result}, but got {result_b}")

    def test_integration_with_zero_constant(self):
        """
        Tests the integration when the constant is zero (should raise an error).
        """
        # Test input values
        num1, num2 = 20, 10

        # Step 1: Call Component A to subtract num2 from num1
        result_a = subtract_numbers(num1, num2)

        # Step 2: Call Component B with a constant of 0 (should raise ValueError)
        with self.assertRaises(ValueError):
            divide_by_constant(result_a, constant=0)

# Run the integration test
if __name__ == "__main__":
    unittest.main()