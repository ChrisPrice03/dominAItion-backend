package com.dominAItionBackend.service;

import com.dominAItionBackend.models.Game;
import com.dominAItionBackend.models.Territory;
import com.dominAItionBackend.repository.GameRepository;
import com.dominAItionBackend.repository.TerritoryRepository;
import com.dominAItionBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

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
    private UserRepository userRepository;

    @Autowired
    TerritoryRepository territoryRepository;

    public String handleStoryRequest(String gameId, String playerId, String request, int difficulty) {
        //pulling game state
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            return "Error: Game not found"; // Game not found
        }

        //constructing prompt for AI
        String prompt = "Game State: " + extractRelevantGameState(game) + "\n"
                + "Player ID: " + playerId + "\n"
                + "Request: " + request + "\n"
                + "Provide the next event in the game based on the current state and request.";

        if (difficulty == 1) {
            prompt += "Ensure the player gets what they want to some degree. Make their chance of success slightly high";
        }
        else if (difficulty == 2) {
            prompt += "Ensure the player has a fair chance of getting what they want, but not without resistance or pushback";
        }
        else if (difficulty == 3) {
            prompt += "Ensure the player has a difficult time getting what they want. Add many challenges and make it super hard for them to acheive their goal";
        }

        System.out.println("Prompt sent to AI:\n" + prompt);

        String response = aiService.callOrchestrationAgent(prompt);
        System.out.println("Response from AI:\n" + response);

        String storyUpdate = interpretAgentResponse(game, response);
        return storyUpdate;
    }

    public String extractRelevantGameState(Game game) {
        List<String> playerIds = game.getPlayerIds();
        List<Map<String, Object>> territoryInfo = getTerritoriesByGameId(game.getId());
        String gameLog = game.getGame_log();

        //getting player names
        List<Map<String, String>> playerInfoList = new ArrayList<>();

        for (String playerId : playerIds) {
            userRepository.findById(playerId).ifPresent(user -> {
                Map<String, String> playerInfo = new HashMap<>();
                playerInfo.put("id", user.getId());
                playerInfo.put("name", user.getUsername());
                playerInfoList.add(playerInfo);
            });
        }

        return "Players: " + playerInfoList.toString() + "\n"
                + "Territories: " + territoryInfo.toString() + "\n"
                + "Game Log: " + gameLog;
    }

    public String interpretAgentResponse(Game game, String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(response);

            String jsonified = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
            System.out.println("Full JSON:\n" + jsonified);

            //updating territory ownership
            JsonNode territoryList = root.path("territoryList");

            if (territoryList.isArray()) {
                for (JsonNode territoryNode : territoryList) {
                    String territoryId = territoryNode.path("territoryId").asText(null);
                    String ownerId = territoryNode.path("ownerId").isNull() ? null : territoryNode.path("ownerId").asText();

                    if (territoryId != null) {
                        territoryRepository.findById(territoryId).ifPresent(territory -> {
                            territory.setOwnerID(ownerId);
                            territoryRepository.save(territory);
                        });
                    }
                }
            }

            //updating player points
            Map<String, Integer> playerPoints = new HashMap<>();
            List<Territory> allTerritories = territoryRepository.findByGameId(game.getId());

            for (Territory territory : allTerritories) {
                String ownerId = territory.getOwnerID();
                if (ownerId != null) {
                    playerPoints.put(ownerId, playerPoints.getOrDefault(ownerId, 0) + territory.getPointVal());
                }
            }

            game.setPlayerPoints(playerPoints);

            // updating game log
            String logUpdate = root.path("log_note").asText("");
            if (!logUpdate.isEmpty()) {
                game.addToGameLog(logUpdate);
            }

            // Check for a winner
            Optional<Map.Entry<String, Integer>> winner = playerPoints.entrySet().stream()
                    .filter(entry -> entry.getValue() >= game.getWinningPoints())
                    .findFirst();

            if (winner.isPresent()) {
                String winningPlayerId = winner.get().getKey();
                game.addToGameLog("Player " + winningPlayerId + " has won the game with " + winner.get().getValue() + " points!");
                System.out.println("Player " + winningPlayerId + " has reached " + winner.get().getValue() + " points and wins the game!");
                game.setStatus("Completed");

                String summary = aiService.callSummaryAgent(game.getGame_log());
                game.setSummary(summary);
            }

            // Save updated game state
            gameRepository.save(game);

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
            details.put("territoryId", territory.getId());
            details.put("territoryName", territory.getTerritoryName());
            details.put("pointValue", territory.getPointVal());
            details.put("ownerId", territory.getOwnerID());
            territoryDetails.add(details);
        }
        return territoryDetails;
    }

    public boolean addPlayerToGame(String gameId, String playerId) {
        // Fetch the game by its ID
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            return false; // Game not found
        }

        // Add the player to the game's player list if not already present
        if (!game.getPlayerIds().contains(playerId)) {
            game.getPlayerIds().add(playerId);
            gameRepository.save(game);
        }

        return true; // Player successfully added
    }

    public boolean startGame(String gameId) {
        // Fetch the game by its ID
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            return false; // Game not found
        }

        // Update the game's status to "ongoing"
        game.setStatus("ongoing");
        gameRepository.save(game);

        return true; // Game successfully started
    }

    public Map<String, Object> getGameInfo(String gameId) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            return Collections.emptyMap(); // Game not found
        }

        Map<String, Object> gameInfo = new HashMap<>();
        gameInfo.put("gameId", game.getId());
        gameInfo.put("worldId", game.getWorldId());
        gameInfo.put("status", game.getStatus());
        gameInfo.put("playerIds", game.getPlayerIds());
        gameInfo.put("playerPoints", game.getPlayerPoints());
        gameInfo.put("winningPoints", game.getWinningPoints());
        gameInfo.put("gameLog", game.getGame_log());

        //creating summary if game is not completed
        if (!game.getStatus().equals("Completed") || game.getSummary() == null || game.getSummary().isEmpty()) {
            String summary = aiService.callSummaryAgent(game.getGame_log());
            game.setSummary(summary);
            gameRepository.save(game);
        }

        gameInfo.put("summary", game.getSummary());

        return gameInfo;
    }
}
