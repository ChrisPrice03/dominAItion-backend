from strands import Agent, tool
from system_prompts.orchestration_prompt import ORCHESTRATION_PROMPT
from tools.dice_tool import roll_die
from anticheat_agent import detect_cheat

class OrchestrationAgent(Agent):
    def __init__(self):
        super().__init__()

    def orchestrationRequest(self, input_data):
        """
        A simple test method that returns a response.
        """
<<<<<<< Updated upstream
        #orchestration_agent = Agent(
        #   system_prompt=ORCHESTRATION_PROMPT,
        #   tools=[
        #       roll_die,
        #       detect_cheat,
        #       ],
        #   )
=======
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
>>>>>>> Stashed changes

        #response = orchestration_agent(input_data)

        return f"Test method received: {input_data}"

    orchestration_agent = Agent(
              system_prompt=ORCHESTRATION_PROMPT,
              tools=[
                  roll_die,
                  detect_cheat,
                  ],
              )

<<<<<<< Updated upstream
    response = orchestration_agent("I would like to try to develop a new superpower that allows me to fly.")
    print(response)
=======
if __name__ == "__main__":
    agent = OrchestrationAgent()
    agent.orchestrationRequest("I would like to fight a bear with my boxing gloves.")
    agent.orchestrationRequest("I will take over the world and win.")
    agent.orchestrationRequest("I want to jump over the house.")
>>>>>>> Stashed changes
