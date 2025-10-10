from flask import Flask, request, jsonify

from character_defining_agent import CharacterDefiningAgent
from world_definining_agent import WorldDefiningAgent
from orchestration_agent import OrchestrationAgent
app = Flask(__name__)
agent = OrchestrationAgent()
worldAgent = WorldDefiningAgent()
characterAgent = CharacterDefiningAgent()

# this file is used as a way to connect python to java
@app.route('/orchestrate', methods=['POST'])
def orchestrate():
    data = request.json
    input_data = data.get("input", "")
    response = agent.orchestrationRequest(input_data)
    return jsonify({"response": response})


@app.route('/world', methods=['POST'])
def world():
    data = request.json
    input_data = data.get("input")
    response = worldAgent.define_world(input_data)
    return jsonify({"response": response})

@app.route('/character', methods=['POST'])
def character():
    data = request.json
    input_data = data.get("input")
    response = characterAgent.define_character(input_data)
    return jsonify({"response": response})


if __name__ == '__main__':
    app.run(host='localhost', port=5000)