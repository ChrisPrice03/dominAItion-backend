package com.dominAItionBackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dominAItionBackend.models.Character;
import com.dominAItionBackend.repository.CharacterRepository;

@Service
public class CharacterService {

    @Autowired
    private CharacterRepository characterRepository;

    //public void removeFriends(String userId, String friendId) {

    //}
}
