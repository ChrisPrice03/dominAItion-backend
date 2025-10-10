from strands import tool
import random

@tool
def roll_die(sides: int) -> int:
    """
    Rolls a die with the specified number of sides.

    Args:
        sides (int): The number of sides on the die.

    Returns:
        int: The result of the die roll.
    """
    if sides < 1:
        return ValueError("A die must have at least one side.")

    result = random.randint(1, sides)
    print(f"Rolled a {sides}-sided die: {result}")
    return result

if __name__ == "__main__":
    result = roll_die(6)
    print(result)
    result = roll_die(20)
    print(result)
    result = roll_die(0)
    print(result)

