from strands import Agent, tool
from strands.models.openai import OpenAIModel
from system_prompts.summary_prompt import SUMMARY_PROMPT
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

class SummaryAgent(Agent):
    def __init__(self):
        super().__init__()

    def summaryRequest(self, input_data):
        model = OpenAIModel(
            client_args={
                "api_key": API_KEY,
            },
            model_id="gpt-5-nano",
            params={
                "max_completion_tokens": 8000,

            }
        )

        summary_agent = Agent(
            model=model,
            system_prompt=SUMMARY_PROMPT,
        )

        response = summary_agent(input_data)

        return str(response)

if __name__ == "__main__":
    agent = SummaryAgent()
    agent.summaryRequest("""
    2025-11-02T14:10:01.312988700: Attack on Washington failed; no territory changes hands.
    2025-11-02T14:12:53.781174300: HarrisB's attack on Washington failed again; no territory changes hands and the attacker withdraws with light casualties.
    2025-11-02T14:14:07.843960800: HarrisB's attack on Washington failed again; no territory changes hands.
    2025-11-02T14:15:27.532916200: HarrisB conquers Oregon; ownership of Oregon updated to HarrisB.
    """)
    #agent.orchestrationRequest("I will take over the world and win.")
    #agent.orchestrationRequest("I want to jump over the house, then talk to Jimmy.")

