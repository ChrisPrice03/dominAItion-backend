from strands import Agent, tool
from system_prompts import ANTICHEAT_PROMPT

@tool
def detect_cheat(action: str) -> str:
    """
    Detects if the given action is considered cheating in the game context.

    Args:
        action (str): The action to evaluate.

    Returns:
        str: plaintext of the agent's conclusion on whether the action is cheating or not.
    """
    try:
            anticheat_agent = Agent(
                system_prompt=ANTICHEAT_PROMPT,
            )

            response = anticheat_agent(action)
            return str(response)
        except Exception as e:
            return f"Error in anticheat_agent: {str(e)}"