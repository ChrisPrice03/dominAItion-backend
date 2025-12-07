package com.dominAItionBackend.controllers;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.dominAItionBackend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dominAItionBackend.models.Lobby;
import com.dominAItionBackend.models.User;
import com.dominAItionBackend.repository.LobbyRepository;
import com.dominAItionBackend.repository.UserRepository;
import com.dominAItionBackend.dto.JoinLobby;

@RestController
@RequestMapping("/api/lobby")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "https://dominaition-frontend.onrender.com"
})
public class LobbySocketController {

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private GameService gameService;

    // Get a lobby by ID
    @GetMapping("/{id}")
    public Lobby getLobby(@PathVariable String id) {
        return lobbyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lobby not found: " + id));
    }

    // Get all lobbies
    @GetMapping
    public List<Lobby> getAllLobbies() {
        return lobbyRepository.findAll()
                          .stream()
                          .filter(lobby -> !lobby.isPrivateLobby())
                          .toList();
    }

    // Create a new lobby
    @PostMapping
    public Lobby createLobby(@RequestBody Lobby lobbyRequest) {
        // Generate random 6-character code
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }

        System.out.println("Received lobbyRequest.isPrivate: " + lobbyRequest.isPrivateLobby());
        System.out.println(lobbyRequest.getMap());
        lobbyRequest.setCode(code.toString());

        if (lobbyRequest.getUsers() == null) {
            lobbyRequest.setUsers(new java.util.ArrayList<>());
        }

        Lobby savedLobby = lobbyRepository.save(lobbyRequest);
        System.out.println("Saved lobby private: " + savedLobby.isPrivateLobby());
        return savedLobby;
    }

    // Get a lobby by code
    @GetMapping("/code/{code}")
    public ResponseEntity<Lobby> getLobbyByCode(@PathVariable String code) {
        Optional<Lobby> lobby = lobbyRepository.findByCode(code);
        return lobby.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // WebSocket: user joined
    @MessageMapping("/lobby/join")
    public void userJoined(JoinLobby message) {
        String lobbyId = message.getLobbyId();
        String userId = message.getUserId();
        String characterId = message.getCharacterId();

        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new RuntimeException("Lobby not found: " + lobbyId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // 1) Add user + store character
        lobby.addUserWithCharacter(user, characterId);
        lobbyRepository.save(lobby);

        // 2) If game already exists, auto-add to that game
        if (lobby.getGameId() != null && characterId != null && !characterId.isBlank()) {
            gameService.addPlayerToGame(lobby.getGameId(), userId, characterId);
        }

        // 3) Broadcast updated lobby
        messagingTemplate.convertAndSend("/topic/lobby/" + lobby.getId(), lobby);
    }
}
