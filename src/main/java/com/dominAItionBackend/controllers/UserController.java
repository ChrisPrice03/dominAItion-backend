package com.dominAItionBackend.controllers;

import com.dominAItionBackend.models.User;
import com.dominAItionBackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000") // allow React frontend
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // optional: check duplicates
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User updated) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setUsername(updated.getUsername());
                    existing.setBio(updated.getBio());
                    existing.setIcon(updated.getIcon());
                    existing.setPublic(updated.isPublic());
                    return ResponseEntity.ok(userRepository.save(existing));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    @PutMapping("/addFriend/{email}/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable String email, @PathVariable String friendId) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Initialize friend list if null
        if (user.getFriendIds() == null) {
            user.setFriendIds(new ArrayList<>());
        }

        // Add friend only if not already present
        if (!user.getFriendIds().contains(friendId)) {
            user.getFriendIds().add(friendId);
            userRepository.save(user);
        }

        return ResponseEntity.ok(user);
    }

    @PutMapping("/removeFriend/{email}/{friendId}")
    public ResponseEntity<User> removeFriend(@PathVariable String email, @PathVariable String friendId) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // If the user has friends, remove the given ID
        if (user.getFriendIds() != null && user.getFriendIds().contains(friendId)) {
            user.getFriendIds().remove(friendId);
            userRepository.save(user);
        }

        return ResponseEntity.ok(user);
    }

    @PutMapping("/updatePassword/{email}")
    public ResponseEntity<User> updatePassword(@PathVariable String email, @RequestBody String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // hash the password before saving
        String hashed = passwordEncoder.encode(newPassword);
        user.setPassword(hashed);
        userRepository.save(user);

        return ResponseEntity.ok(user);
    }
}
