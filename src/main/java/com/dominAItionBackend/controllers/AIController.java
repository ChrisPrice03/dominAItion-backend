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
    @PostMapping("/story")
    public String promptRespond(@RequestBody Map<String, String> requestBody) {
        String request = requestBody.get("request");
        return gameService.handleStoryRequest(request);
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
