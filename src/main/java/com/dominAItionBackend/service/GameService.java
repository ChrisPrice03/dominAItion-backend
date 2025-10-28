package com.dominAItionBackend.service;

import com.dominAItionBackend.models.Game;
import com.dominAItionBackend.models.Territory;
import com.dominAItionBackend.repository.GameRepository;
import com.dominAItionBackend.repository.TerritoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GameService {
    //this class will hold game logic and game state management

    @Autowired
    private AIService aiService;

    @Autowired
    private WorldService worldService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    TerritoryRepository territoryRepository;

    public String handleStoryRequest(String input) {
        //call an internal function to pull relevant game state
        String response = aiService.callOrchestrationAgent(input);
        String storyUpdate = interpretAgentResponse(response);
        return storyUpdate;
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

    public String createGame(String worldId, int winningPoints) {
        Game newGame = new Game(worldId, winningPoints);

        //generate territories for game
        List<String> territories = worldService.generateTerritories(worldId);
        newGame.setTerritoryIds(territories);

        Game savedGame = gameRepository.save(newGame);
        String gameId = savedGame.getId();

        //updating territories with gameId
        for (String territoryId : territories) {
            Territory territory = territoryRepository.findById(territoryId).orElse(null);
            if (territory != null) {
                territory.setGameID(gameId);
                territoryRepository.save(territory);
            }
        }

        return gameId;
    }

    public List<Map<String, Object>> getTerritoriesByGameId(String gameId) {
        List<Territory> territories = territoryRepository.findByGameId(gameId);

        // Map territories to a list of details
        List<Map<String, Object>> territoryDetails = new ArrayList<>();
        for (Territory territory : territories) {
            Map<String, Object> details = new HashMap<>();
            details.put("territoryName", territory.getTerritoryName());
            details.put("pointValue", territory.getPointVal());
            details.put("ownerId", territory.getOwnerID());
            territoryDetails.add(details);
        }
        return territoryDetails;
    }
}
