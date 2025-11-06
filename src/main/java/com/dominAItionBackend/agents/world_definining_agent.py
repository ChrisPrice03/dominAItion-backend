from strands import Agent, tool
from strands.models.openai import OpenAIModel
from system_prompts.world_defining_prompt import WORLD_DEFINING_PROMPT
import os

# --- API key setup ---
try:
    # Try importing local file first (for local development)
    from api_key import API_KEY
except ModuleNotFoundError:
    # Fallback to environment variable (for deployment)
    API_KEY = os.environ.get("OPENAI_API_KEY")
if not API_KEY:
    raise ValueError("OpenAI API key not found. Set it in api_key.py or OPENAI_API_KEY environment variable.")

class WorldDefiningAgent(Agent):
    def init(self):
        super().init()
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
                            "max_completion_tokens": 5000,
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
        
    
if __name__ == "__main__":
    agent = WorldDefiningAgent()
    result = agent.define_world("I would like a desert wasteland")

    print(result)
    result = agent.define_world("I would like an ocean world")
    
    print(result)
    result = agent.define_world("I would like a medieval world")
    
    print(result)