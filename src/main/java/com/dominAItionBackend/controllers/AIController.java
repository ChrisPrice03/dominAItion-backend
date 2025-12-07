package com.dominAItionBackend.controllers;

import com.dominAItionBackend.repository.GameRepository;
import com.dominAItionBackend.repository.UserRepository;
import com.dominAItionBackend.controllers.UserController;
import com.dominAItionBackend.models.Game;
import com.dominAItionBackend.models.User;
import com.dominAItionBackend.models.Message;
import com.dominAItionBackend.service.AIService;
import com.dominAItionBackend.service.CharacterService;
import com.dominAItionBackend.service.GameService;
import com.dominAItionBackend.service.WorldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    @Autowired
    private AIService aiService;

    @Autowired
    private GameService gameService;

    @Autowired
    private WorldService worldService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserController userController;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

@Autowired
    private CharacterService characterService;

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
    String color = requestBody.get("color");
    int difficulty = Integer.parseInt(requestBody.get("difficulty"));

    // Main response logic
    String response = gameService.handleStoryRequest(gameId, playerId, request, difficulty);

    Optional<User> user = userRepository.findById(playerId);

    String username = user.map(User::getUsername).orElse(null);


    // Update game turn + storyboard
    Game game = gameRepository.findGameById(gameId);
    game.setTurn(game.getTurn() + 1);
    game.setStoryboard(response);

    Message message = new Message(username, color, request, gameId, response);

    game.getGameLog().add(message);

    gameRepository.save(game);

    messagingTemplate.convertAndSend(
        "/topic/game/" + gameId + "/refresh",
        "reload"
    );

    return response;
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
        String userId = requestBody.get("userId");
        String request = requestBody.get("request");
        return characterService.handleCharacterRequest(userId, request);
    }
}
