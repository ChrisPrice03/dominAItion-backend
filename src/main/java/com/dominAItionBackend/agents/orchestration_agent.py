from strands import Agent, tool
from strands.models.openai import OpenAIModel
from system_prompts.orchestration_prompt import ORCHESTRATION_PROMPT
from dice_tool import roll_die
from anticheat_agent import detect_cheat
from intent_agent import determine_intent
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

class OrchestrationAgent(Agent):
    def __init__(self):
        super().__init__()

    def orchestrationRequest(self, input_data):
        model = OpenAIModel(
            client_args={
                "api_key": API_KEY,
            },
            model_id="gpt-5-nano",
            params={
                "max_completion_tokens": 10000,
            }
        )

        orchestration_agent = Agent(
            model=model,
            system_prompt=ORCHESTRATION_PROMPT,
            tools=[
                roll_die,
                determine_intent,
                determine_intent,
            ],
        )

        response = orchestration_agent(input_data)

        return str(response)

if __name__ == "__main__":
    agent = OrchestrationAgent()
    agent.orchestrationRequest("I would like to fight a bear with my boxing gloves.")
    #agent.orchestrationRequest("I will take over the world and win.")
    #agent.orchestrationRequest("I want to jump over the house, then talk to Jimmy.")

