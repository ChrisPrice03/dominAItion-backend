package com.dominAItionBackend.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dominAItionBackend.models.Game;
import com.dominAItionBackend.models.Lobby;
import com.dominAItionBackend.models.Message;
import com.dominAItionBackend.models.User;
import com.dominAItionBackend.repository.GameRepository;
import com.dominAItionBackend.repository.LobbyRepository;
import com.dominAItionBackend.repository.MessageRepository;
import com.dominAItionBackend.repository.UserRepository;
import com.dominAItionBackend.service.GameService;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public String createGame(@RequestBody Map<String, String> requestBody) {
        String worldId = "69079f300a402a2b69a147d1";

        String wp = requestBody.get("winningPoints");
        int winningPoints = 100; // default
        if (wp != null) {
            try {
                winningPoints = Integer.parseInt(wp);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid winningPoints: " + wp);
            }
        }

        // 1) Create the game
        String gameId = gameService.createGame(worldId, winningPoints);

        // 2) Fetch lobby
        String lobbyId = requestBody.get("lobbyId");
        if (lobbyId == null || lobbyId.isEmpty()) {
            throw new RuntimeException("LobbyId missing");
        }

        Lobby lobby = lobbyRepository.findLobbyById(lobbyId);
        if (lobby == null) throw new RuntimeException("Lobby not found: " + lobbyId);

        List<User> users = lobby.getUsers();
        if (users == null || users.isEmpty()) throw new RuntimeException("Lobby has no users");

        // 3) (Optional but recommended) store gameId on the lobby
        lobby.setGameId(gameId);
        lobbyRepository.save(lobby);

        // 4) Add all lobby users to the game using their chosen character
        for (User user : users) {
            String playerId = user.getId();

            // look up the character they chose when they joined
            String characterId = lobby.getUserCharacterMap().get(playerId);

            if (characterId == null || characterId.isBlank()) {
                // up to you: either skip, or throw, or use a default
                System.out.println("No character selected for user " + playerId + ", skipping addPlayer");
                continue;
            }

            // NEW: playerId + characterId
            gameService.addPlayerToGame(gameId, playerId, characterId);
        }

        // 5) Broadcast to clients (same as before)
        Map<String, String> payload = new HashMap<>();
        payload.put("gameId", gameId);
        messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId + "/gameCreated", payload);
        System.out.println("Broadcasted gameCreated for lobby " + lobbyId);

        return gameId;
    }




    @PostMapping("/territories")
    public List<Map<String, Object>> getTerritories(@RequestBody Map<String, String> requestBody) {
        String gameId = requestBody.get("gameId");
        return gameService.getTerritoriesByGameId(gameId);
    }

    /**
     * Adds a player to a game.
     * Example Request Body:
     * {
     *     "gameId": "68fa872b8636ca5f883a4a1a",
     *     "playerId": "player123"
     *     "characterId": "char456"
     * }
     */
    @PostMapping("/addPlayer")
    public boolean addPlayerToGame(@RequestBody Map<String, String> requestBody) {
        String gameId = requestBody.get("gameId");
        String playerId = requestBody.get("playerId");
        String characterId = requestBody.get("characterId");
        return gameService.addPlayerToGame(gameId, playerId, characterId);
    }

    /**
     * Starts a game.
     * Example Request Body:
     * {
     *     "gameId": "68fa872b8636ca5f883a4a1a"
     * }
     */
    @PostMapping("/start")
    public boolean startGame(@RequestBody Map<String, String> requestBody) {
        String gameId = requestBody.get("gameId");
        return gameService.startGame(gameId);
    }

    @PostMapping("/win")
    public String win(@RequestBody Map<String, String> requestBody) {
        String gameId = requestBody.get("gameId");
        String winnerId = requestBody.get("winnerId");

        Game game = gameRepository.findGameById(gameId);

        game.setWinnerId(winnerId);

        game.setStatus("done");
        
        gameRepository.save(game);
        return gameId;

    }

    @PostMapping("/message")
    public String sendMessage(@RequestBody Map<String, String> requestBody) {
        String gameId = requestBody.get("gameId");
        String name = requestBody.get("name");
        String color = requestBody.get("color");
        String contents = requestBody.get("contents");

        Message message = new Message(name, color, contents, gameId);
        String messageId = message.getId();

        Game game = gameRepository.findGameById(gameId);

        messageRepository.save(message);

        game.getChat().add(message);

        gameRepository.save(game);

        messagingTemplate.convertAndSend(
            "/topic/game/" + gameId + "/refresh",
            "reload"
        );

        return messageId;
    }

    /**
     * Gets game information.
     * Example Request Body:
     * {
     *    "gameId": "68fa872b8636ca5f883a4a1a"
     * }
     */
    @PostMapping("/getInfo")
    public Map<String, Object> getGameInfo(@RequestBody Map<String, String> requestBody) {
    String gameId = requestBody.get("gameId");
    
    Game game = gameRepository.findGameById(gameId);
    if (game == null) {
        throw new RuntimeException("Game not found for ID: " + gameId);
    }

     // Suppose you have a Game object with playerIds
List<String> playerIds = game.getPlayerIds();

// Fetch all players from DB
Map<String, String> playerNames = new HashMap<>();
for (String id : playerIds) {
    userRepository.findById(id).ifPresent(user -> playerNames.put(id, user.getUsername()));
}

    Map<String, Object> info = new HashMap<>();
    info.put("gameId", game.getId());
    info.put("playerIds", game.getPlayerIds());
    info.put("spectatorIds", game.getSpectatorIds());
    info.put("status", game.getStatus());
    info.put("summary", game.getSummary());
    info.put("winningPoints", game.getWinningPoints());
    info.put("worldId", game.getWorldId());
    info.put("turn", game.getTurn());
    info.put("storyboard", game.getStoryboard());
    info.put("playerPoints", game.getPlayerPoints());
    info.put("chat", game.getChat());
    info.put("playerNames", playerNames);


    // Add territories
    List<Map<String, Object>> territories = gameService.getTerritoriesByGameId(gameId);
    info.put("territories", territories);

    return info;
}

@GetMapping("/active")
public List<Game> getActiveGames() {
    return gameRepository.findAllByStatusNotDone();
}

@PostMapping("/addSpectator")
public void addSpectator(@RequestBody Map<String, String> body) {
    String gameId = body.get("gameId");
    String userId = body.get("userId");

    Game game = gameRepository.findGameById(gameId);
    if (game == null) throw new RuntimeException("Game not found");

    if (!game.getSpectatorIds().contains(userId)) {
        game.getSpectatorIds().add(userId);
        gameRepository.save(game);
    }
}

@PostMapping("/removeSpectator")
public void removeSpectator(@RequestBody Map<String, String> body) {
    String gameId = body.get("gameId");
    String userId = body.get("userId");

    Game game = gameRepository.findGameById(gameId);
    if (game == null) throw new RuntimeException("Game not found");

    game.getSpectatorIds().remove(userId);
    gameRepository.save(game);
}

@GetMapping("/spectators/names/{gameId}")
public ResponseEntity<Map<String, String>> getSpectatorNames(@PathVariable String gameId) {
    Game game = gameRepository.findGameById(gameId);
    if (game == null) {
        return ResponseEntity.notFound().build();
    }

    List<String> spectatorIds = new ArrayList<>(game.getSpectatorIds());
    Map<String, String> result = new HashMap<>();

    for (String id : spectatorIds) {
        userRepository.findById(id).ifPresent(user -> result.put(id, user.getUsername()));
    }

    return ResponseEntity.ok(result);
}

@GetMapping("/username")
public String getUsername(@RequestParam String userId) {
    return userRepository.findById(userId)
            .map(User::getUsername)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
}


    
}
