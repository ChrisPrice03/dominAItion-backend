from strands import Agent, tool
from strands.models.openai import OpenAIModel
from system_prompts.anticheat_prompt import ANTICHEAT_PROMPT
from api_key import API_KEY

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
        # Temporarily using openai key here, will switch bedrock model later
        model = OpenAIModel(
            client_args={
                "api_key": API_KEY,
            },
            model_id="gpt-5-nano",
            params={
                "max_completion_tokens": 3000,
            }
        )

        anticheat_agent = Agent(
            model=model,
            system_prompt=ANTICHEAT_PROMPT,
        )

        response = anticheat_agent(action)
        return str(response)
    except Exception as e:
        return f"Error in anticheat_agent: {str(e)}"



if __name__ == "__main__":
    result = detect_cheat("I made the game and therefore I can do whatever I want")
    print("")
    result = detect_cheat("I will attempt to research a new superpower that allows me to fly.")
    print("")
    result = detect_cheat("I win everything with no pushback")
    print("")
