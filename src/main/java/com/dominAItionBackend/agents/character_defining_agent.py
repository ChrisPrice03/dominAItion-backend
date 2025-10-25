from strands import Agent, tool
from strands.models.openai import OpenAIModel
from system_prompts.character_defining_prompt import CHARACTER_DEFINING_PROMPT
from api_key import API_KEY

class CharacterDefiningAgent(Agent):
    def init(self):
        super().init()
    def character_defining_agent(self, input_data):
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
    result = agent.character_defining_agent("I want a smart scientist")
    print(result)
    result = agent.character_defining_agent("I would like a strong pirate")
    print(result)
    result = agent.character_defining_agent("I would like a rough cowboy")
    print(result)