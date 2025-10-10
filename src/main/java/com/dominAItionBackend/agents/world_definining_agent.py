from strands import Agent, tool
from strands.models.openai import OpenAIModel
from system_prompts.world_defining_prompt import WORLD_DEFINING_PROMPT
from api_key import API_KEY

class WorldDefiningAgent(Agent):
    def __init__(self):
        super().__init__()
    def define_world(self, input_data):
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
                    system_prompt=WORLD_DEFINING_PROMPT,
                )

                response = world_defining_agent(input_data)
                return str(response)
        except Exception as e:
                return f"Error in world_defining_agent: {str(e)}"