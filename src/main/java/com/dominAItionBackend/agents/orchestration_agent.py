from strands import Agent, tool
from system_prompts import ORCHESTRATION_AGENT_PROMPT
from tools.dice_tool import roll_die
from agents.anticheat_agent import detect_cheat

class OrchestrationAgent(Agent):
    def __init__(self):
        super().__init__()

    def orchestrationRequest(self, input_data):
        """
        A simple test method that returns a response.
        """
        #orchestration_agent = Agent(
        #   system_prompt=ORCHESTRATION_AGENT_PROMPT,
        #   tools=[
        #       roll_die,
        #       detect_cheat,
        #       ],
        #   )

        #response = orchestration_agent(input_data)

        return f"Test method received: {input_data}"