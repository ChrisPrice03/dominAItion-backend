from strands import Agent, tool
from strands.models.openai import OpenAIModel
from system_prompts.territory_prompt import TERRITORY_PROMPT
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

class TerritoryAgent(Agent):
    def __init__(self):
        super().__init__()

    def territoryRequest(self, input_data):
        model = OpenAIModel(
            client_args={
                "api_key": API_KEY,
            },
            model_id="gpt-5-nano",
            params={
                "max_completion_tokens": 5000,

            }
        )

        territory_agent = Agent(
            model=model,
            system_prompt=TERRITORY_PROMPT,
        )

        response = territory_agent(input_data)

        return str(response)

if __name__ == "__main__":
    agent = TerritoryAgent()
    agent.territoryRequest("This is a map of the US")


