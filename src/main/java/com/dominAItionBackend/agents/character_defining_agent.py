from strands import Agent, tool
from strands.models.openai import OpenAIModel
from system_prompts.character_defining_prompt import CHARACTER_DEFINING_PROMPT
from api_key import API_KEY

@tool
def define_world(action: str) -> str:
    """
    Takes in an input and gives the world qualities such as resources, weather, environment, etc.

    Args:
        action (str): These are some characteristics the world should have

    Returns:
        str: plaintext of the world qualities
    """
    try:
            # Temporarily using openai key here, will switch bedrock model later
            model = OpenAIModel(
                    client_args={
                        "api_key": API_KEY,
                    },
                    model_id="gpt-5-nano",
                    params={
                        "max_completion_tokens": 2000,
                    }
                )

            world_defining_agent = Agent(
                model=model,
                system_prompt=CHARACTER_DEFINING_PROMPT,
            )

            response = character_defining_agent(action)
            return str(response)
    except Exception as e:
            return f"Error in world_defining_agent: {str(e)}"