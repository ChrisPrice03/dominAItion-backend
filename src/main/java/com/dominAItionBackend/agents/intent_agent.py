from strands import Agent, tool
from strands.models.openai import OpenAIModel
from system_prompts.intent_prompt import INTENT_PROMPT
from api_key import API_KEY

"test "
@tool
def determine_intent(action: str) -> str:
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

        intent_agent = Agent(
            model=model,
            system_prompt=INTENT_PROMPT,
        )

        response = intent_agent(action)
        return str(response)
    except Exception as e:
        return f"Error in intent_agent: {str(e)}"



if __name__ == "__main__":
    result = determine_intent("I would like to fight a bear with my boxing gloves.")
    print("")
    result = determine_intent("I want to climb the mountain then enter the room.")
    print("")
    result = determine_intent("I will fight the dragon, steal its treasure, and rescue the princess.")
    print("")
