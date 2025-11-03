package com.dominAItionBackend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dominAItionBackend.models.User;
import com.dominAItionBackend.repository.UserRepository;
import com.dominAItionBackend.dto.JoinLobby;
import com.dominAItionBackend.models.Lobby;
import com.dominAItionBackend.repository.LobbyRepository;

@RestController
@RequestMapping("/api/lobby")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "https://dominaition-frontend.onrender.com"
})
public class LobbySocketController {

    @Autowired 
    private UserRepository userRepository;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/{id}")
    public Lobby getLobby(@PathVariable String id) {
        return lobbyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lobby not found: " + id));
    }

    @PostMapping
    public Lobby createLobby(@RequestBody Lobby lobbyRequest) {
        if (lobbyRequest.getUsers() == null) {
            lobbyRequest.setUsers(new java.util.ArrayList<>());
        }
        return lobbyRepository.save(lobbyRequest);
    }

    @MessageMapping("/lobby/join")
    public void userJoined(JoinLobby message) {
        String lobbyId = message.getLobbyId();
        String userId = message.getUserId();

        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new RuntimeException("Lobby not found: " + lobbyId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        lobby.addUser(user);
        lobbyRepository.save(lobby);

        // Broadcast full user list to all subscribers
        messagingTemplate.convertAndSend("/topic/lobby/" + lobby.getId(), lobby.getUsers());
    }
}