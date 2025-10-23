package com.dominAItionBackend.controllers;

import com.dominAItionBackend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {
    @Autowired
    private GameService gameService;

    // game creation endpoint (/api/game/create)
    /**
     * Handles world definition requests.
     * Example Request Body:
     * {
     *     "worldId": "68fa872b8636ca5f883a4a1a",
     *     "winningPoints": 20
     * }
     */
    @PostMapping("/create")
    public String createGame(@RequestBody Map<String, String> requestBody) {
        String worldId = requestBody.get("worldId");
        int winningPoints = Integer.parseInt(requestBody.get("winningPoints"));
        return gameService.createGame(worldId, winningPoints);
    }

}
