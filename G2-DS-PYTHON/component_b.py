# component_b.py
def divide_by_constant(result_from_a, constant=5):
    """
    Divides the result from Component A by a constant value.

    Args:
        result_from_a (int or float): The result from Component A.
        constant (int or float): The constant divisor (default is 5).

    Returns:
        int or float: The result of result_from_a / constant.
    """
    if constant == 0:
        raise ValueError("Constant cannot be zero.")
    return result_from_a / constant