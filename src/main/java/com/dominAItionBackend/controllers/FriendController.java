package com.dominAItionBackend.controllers;
import com.dominAItionBackend.models.User;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dominAItionBackend.service.FriendService;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    @Autowired
    private FriendService friendService;
    
    // ✅ Retrieve all friends for a given user
    @GetMapping("/{userId}")
    public ResponseEntity<List<User>> getAllFriends(@PathVariable String userId) {
        List<User> friends = friendService.getFriends(userId);
        return ResponseEntity.ok(friends);
    }

    @DeleteMapping("/{userId}/{friendId}")
    public ResponseEntity<Void> removeFriend(
            @PathVariable String userId,
            @PathVariable String friendId) {
        friendService.removeFriends(userId, friendId);
        return ResponseEntity.noContent().build();
    }
}
