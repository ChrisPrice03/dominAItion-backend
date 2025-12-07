package com.dominAItionBackend.controllers;

import com.dominAItionBackend.models.Character;
import com.dominAItionBackend.models.Message;
import com.dominAItionBackend.repository.CharacterRepository;
import com.dominAItionBackend.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;     
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/characters")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "https://dominaition-frontend.onrender.com"
})
public class CharacterController {

    @Autowired
    private CharacterRepository characterRepository;


    // Get all characters for a specific user
    @GetMapping("/{userId}")
    public List<Character> getUserChats(@PathVariable String userId) {
        return characterRepository.findByCreatorID(userId);
    }

    @GetMapping("/delete/{characterId}")
    public ResponseEntity<String> deleteCharacter(@PathVariable String characterId) {
        try {
            characterRepository.deleteById(characterId);
            return ResponseEntity.ok("Character deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting character");
        }
    }


}
