import unittest
from component_a import component_a
from component_b import component_b

class TestIntegration(unittest.TestCase):
    def test_integration(self):
        num1, num2 = 3, 5
        result_a = component_a(num1, num2)
        result_b = component_b(result_a)
        self.assertEqual(result_b, 80)  # (3 + 5) * 10 = 80

if __name__ == "__main__":
    unittest.main()