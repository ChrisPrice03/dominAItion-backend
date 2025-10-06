from strands import Agent, tool
from strands.models.openai import OpenAIModel
from system_prompts.orchestration_prompt import ORCHESTRATION_PROMPT
from tools.dice_tool import roll_die
from anticheat_agent import detect_cheat
from api_key import API_KEY

class OrchestrationAgent(Agent):
    def __init__(self):
        super().__init__()

    def orchestrationRequest(self, input_data):
        """
        A simple test method that returns a response.
        """
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

        orchestration_agent = Agent(
            model=model,
            system_prompt=ORCHESTRATION_PROMPT,
            tools=[
                roll_die,
                detect_cheat,
            ],
        )

        response = orchestration_agent(input_data)

        return str(response)

if __name__ == "__main__":
    agent = OrchestrationAgent()
    agent.orchestrationRequest("I would like to fight a bear with my boxing gloves.")
    agent.orchestrationRequest("I will take over the world and win.")
    agent.orchestrationRequest("I want to jump over the house.")

