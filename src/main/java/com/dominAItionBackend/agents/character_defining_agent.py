from strands import Agent, tool
from strands.models.openai import OpenAIModel
from system_prompts.character_defining_prompt import CHARACTER_DEFINING_PROMPT
from api_key import API_KEY

class CharacterDefiningAgent(Agent):
    def __init__(self):
        super().__init__()
    def define_character(self, input_data):
        """
        Takes in an input and gives character qualities such as strength, intelligence, etc.

        Args:
            action (str): These are some characteristics the world should have

        Returns:
            str: plaintext of the world qualities
            :param input_data:
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

            character_defining_agent = Agent(
                model=model,
                system_prompt=CHARACTER_DEFINING_PROMPT,
            )

            response = character_defining_agent(input_data)
            return str(response)
        except Exception as e:
            return f"Error in world_defining_agent: {str(e)}"

if __name__ == "__main__":
    agent = CharacterDefiningAgent()
    agent.define_character("I would like a strong pirate")
    agent.define_character("I would like a cowboy")
    agent.define_character("I would like a scientist")