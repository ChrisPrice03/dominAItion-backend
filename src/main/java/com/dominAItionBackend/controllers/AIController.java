package com.dominAItionBackend.controllers;

import com.dominAItionBackend.service.AIService;
import com.dominAItionBackend.service.GameService;
import com.dominAItionBackend.service.WorldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    @Autowired
    private AIService aiService;

    @Autowired
    private GameService gameService;

    @Autowired
    private WorldService worldService;

    //main story endpoint (/api/ai/story)

    /**
     * Handles story requests.
     * {
     * "gameId": "68fa872b8636ca5f883a4a1a",
     *  "playerId": "player123"
     *  "request": "Describe the next event in the game"
     * }
     */
    @PostMapping("/story")
    public String promptRespond(@RequestBody Map<String, String> requestBody) {
        String gameId = requestBody.get("gameId");
        String playerId = requestBody.get("playerId");
        String request = requestBody.get("request");
        int difficulty = Integer.parseInt(requestBody.get("difficulty"));
        return gameService.handleStoryRequest(gameId, playerId, request, difficulty);
    }

    //World Defining Endpoint

    /**
     * Handles world definition requests.
     * Example Request Body:
     * {
     *     "userId": "1",
     *     "request": "This is a basic US map"
     * }
     */
    @PostMapping("/world")
    public String worldDefinition(@RequestBody Map<String, String> requestBody) {
        String userId = requestBody.get("userId");
        String request = requestBody.get("request");
        return worldService.handleWorldRequest(userId, request);
    }

    //Character Defining Endpoint
    @PostMapping("/character")
    public String characterDefinition(@RequestBody Map<String, String> requestBody) {
        String request = requestBody.get("request");
        return aiService.callCharacterDefiningAgent(request);
    }
}
