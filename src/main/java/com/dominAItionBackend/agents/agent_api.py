from flask import Flask, request, jsonify

from world_definining_agent import WorldDefiningAgent
from orchestration_agent import OrchestrationAgent
from territory_agent import TerritoryAgent
from summary_agent import SummaryAgent
from character_defining_agent import CharacterDefiningAgent
app = Flask(__name__)
agent = OrchestrationAgent()
worldAgent = WorldDefiningAgent()
territoryAgent = TerritoryAgent()
summaryAgent = SummaryAgent()
characterAgent = CharacterDefiningAgent()

#this file is used as a way to connect python to java
@app.route('/orchestrate', methods=['POST'])
def orchestrate():
    data = request.json
    input_data = data.get("input", "")
    response = agent.orchestrationRequest(input_data)
    return jsonify({"response": response})


@app.route('/world', methods=['POST'])
def world():
    data = request.json
    input_data = data.get("input","")
    response = worldAgent.define_world(input_data)
    return jsonify({"response": response})

@app.route('/territories', methods=['POST'])
def territories():
    data = request.json
    input_data = data.get("input","")
    response = territoryAgent.territoryRequest(input_data)
    return jsonify({"response": response})

@app.route('/summary', methods=['POST'])
def summarize():
    data = request.json
    input_data = data.get("input", "")
    response = summaryAgent.summaryRequest(input_data)
    return jsonify({"response": response})

@app.route('/character', methods=['POST'])
def character():
    data = request.json
    input_data = data.get("input", "")
    response = characterAgent.character_defining_agent(input_data)
    return jsonify({"response": response})


if __name__ == '__main__':
    app.run(host='localhost', port=5000)