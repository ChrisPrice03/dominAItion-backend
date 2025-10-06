package com.dominAItionBackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    //this class will hold game logic and game state management

    @Autowired
    private AIService aiService;
    public String handleStoryRequest(String input) {
        //call an internal function to pull relevant game state
        String response = aiService.callOrchestrationAgent(input);
        //call an internal function to interpret the response and update game state
        return response;
    }
}
