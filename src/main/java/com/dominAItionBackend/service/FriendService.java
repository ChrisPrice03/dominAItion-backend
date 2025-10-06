package com.dominAItionBackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dominAItionBackend.models.User;
import com.dominAItionBackend.repository.UserRepository;

@Service
public class FriendService {

    @Autowired
    private UserRepository userRepository;

    public void removeFriends(String userId, String friendId) {
        User user = userRepository.findById(userId).orElseThrow();
        User friend = userRepository.findById(friendId).orElseThrow();

        user.getFriendIds().remove(friendId);
        friend.getFriendIds().remove(userId);

        userRepository.save(user);
        userRepository.save(friend);
    }
}
