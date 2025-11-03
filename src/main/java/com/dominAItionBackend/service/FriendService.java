package com.dominAItionBackend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dominAItionBackend.models.User;
import com.dominAItionBackend.repository.UserRepository;

@Service
public class FriendService {

    @Autowired
    private UserRepository userRepository;

    // ✅ Retrieve all friends of a user
    public List<User> getFriends(String userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<String> friendIds = user.getFriendIds();

        // Fetch each friend User object
        return friendIds.stream()
                .map(id -> userRepository.findById(id).orElse(null))
                .filter(friend -> friend != null)
                .collect(Collectors.toList());
    }

    public void removeFriends(String userId, String friendId) {
        User user = userRepository.findById(userId).orElseThrow();
        User friend = userRepository.findById(friendId).orElseThrow();

        user.getFriendIds().remove(friendId);
        friend.getFriendIds().remove(userId);

        userRepository.save(user);
        userRepository.save(friend);
    }
}
