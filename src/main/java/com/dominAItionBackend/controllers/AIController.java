package com.dominAItionBackend.controllers;

import com.dominAItionBackend.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    @Autowired
    private AIService aiService;

    //main story endpoint (/api/ai/story)
    @PostMapping("/story")
    public String promptRespond(@RequestBody Map<String, String> requestBody) {
        String request = requestBody.get("request");
        //return aiService.storyPromptRespond(request);
        return aiService.callOrchestrationAgent(request);
    }

    //World Defining Endpoint
    @PostMapping("/world")
    public String worldDefinition(@RequestBody Map<String, String> requestBody) {
        String request = requestBody.get("request");
        return aiService.callWorldDefiningAgent(request);
    }

    //Character Defining Endpoint
    @PostMapping("/character")
    public String characterDefinition(@RequestBody Map<String, String> requestBody) {
        String request = requestBody.get("request");
        return aiService.callCharacterDefiningAgent(request);
    }
}
