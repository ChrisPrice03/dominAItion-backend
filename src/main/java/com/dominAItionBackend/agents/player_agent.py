from strands import Agent, tool
from strands.models.openai import OpenAIModel
from system_prompts.orchestration_prompt import ORCHESTRATION_PROMPT
from dice_tool import roll_die
from anticheat_agent import detect_cheat
from intent_agent import determine_intent
from api_key import API_KEY

class PlayerAgent(Agent):
    def __init__(self):
        super().__init__()

    def makeTurnRequest(self, input_data):
        model = OpenAIModel(
            client_args={
                "api_key": API_KEY,
            },
            model_id="gpt-5-nano",
            params={
                "max_completion_tokens": 8000,

            }
        )

        player_agent = Agent(
            model=model,
            system_prompt=PLAYER_PROMPT,
            tools=[
                roll_die
            ],
        )

        response = player_agent(input_data)

        return str(response)

if __name__ == "__main__":
    agent = PlayerAgent()
    agent.PlayerRequest("Make A Move with an aggressive strategy")
    #agent.PlayerRequest("Make a Move with a defensive strategy.")
    #agent.PlayerRequest("Make a Move with an avoidant strategy")

