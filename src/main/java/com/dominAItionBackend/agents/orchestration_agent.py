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
        model = OpenAIModel(
            client_args={
                "api_key": API_KEY,
            },
            model_id="gpt-5-nano",
            params={
                "max_completion_tokens": 2000,
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

    # Temporarily using openai key here, will switch bedrock model later
#     model = OpenAIModel(
#         client_args={
#             "api_key": API_KEY,
#         },
#         model_id="gpt-5-nano",
#         params={
#             "max_completion_tokens": 2000,
#         }
#     )

#     orchestration_agent = Agent(
#               model=model,
#               system_prompt=ORCHESTRATION_PROMPT,
#               tools=[
#                   roll_die,
#                   detect_cheat,
#                   ],
#               )
#
#     response = orchestration_agent("I would like to try to develop a new superpower that allows me to fly.")
#     print(response)

# if __name__ == "__main__":
#     agent = OrchestrationAgent()
#     print(agent.orchestrationRequest("I would like to research a new technology to allow me to communicate with nations far away from me"))