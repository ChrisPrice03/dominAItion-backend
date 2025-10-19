package com.dominAItionBackend.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dominAItionBackend.models.User;
import com.dominAItionBackend.repository.UserRepository;
import com.dominAItionBackend.models.Lobby;
import com.dominAItionBackend.repository.LobbyRepository;

@RestController
@RequestMapping("/api/lobby")
@CrossOrigin(origins = "http://localhost:3000")
public class LobbySocketController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LobbyRepository lobbyRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Lobby> getLobbyById(@PathVariable String id) {
        Lobby lobby = lobbyRepository.findLobbyById(id); // returns Lobby or null
        if (lobby != null) {
            return ResponseEntity.ok(lobby);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // --- CREATE a new Lobby (starts empty) ---
    @PostMapping
    public ResponseEntity<Lobby> createLobby(@RequestBody Lobby lobbyRequest) {
        // Ensure the lobby starts with no users
        lobbyRequest.setUsers(null); // or new ArrayList<>(), depending on Lobby constructor
        if (lobbyRequest.getUsers() == null) {
            lobbyRequest.setUsers(new java.util.ArrayList<>());
        }

        // Save lobby to MongoDB
        Lobby savedLobby = lobbyRepository.save(lobbyRequest);

        return ResponseEntity.ok(savedLobby);
    }
    


    // This listens for messages from clients at /app/lobby/join
    @MessageMapping("/lobby/join")
    @SendTo("/topic/lobby")
    public String userJoined(String userId) {
        // Simply echo the userId to all subscribers
        return userId;
    }
}
