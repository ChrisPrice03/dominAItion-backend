package com.dominAItionBackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GameService {
    //this class will hold game logic and game state management

    @Autowired
    private AIService aiService;
    public String handleStoryRequest(String input) {
        //call an internal function to pull relevant game state

        String response = aiService.callOrchestrationAgent(input);
        String storyUpdate = interpretAgentResponse(response);
        return storyUpdate;
    }

    public String handleWorldDescriptionRequest(String input) {
        String response = aiService.callWorldDefiningAgent(input);
        return response;
    }

    public String interpretAgentResponse(String response) {
        //TODO: add logic to update game state
        try {
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(response);

            String jsonified = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
            System.out.println("Full JSON:\n" + jsonified);

            return root.path("outcome").asText("No outcome found");
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing AI response";
        }
    }
}
