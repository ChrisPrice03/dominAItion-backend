package com.dominAItionBackend.service;

import com.dominAItionBackend.models.World;
import com.dominAItionBackend.repository.WorldRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorldService {
    //this class will manage world creation

    @Autowired
    private AIService aiService;

    @Autowired
    private WorldRepository worldRepository;

    public String handleWorldRequest(String userId, String input) {
        String response = aiService.callWorldDefiningAgent(input);

        try {
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(response);

            String jsonified = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
            System.out.println("Full JSON:\n" + jsonified);

            String worldName = root.path("name").asText("No world name found");
            String worldDescription = root.path("description").asText("No description found");

            //Save world to database
            World world = new World(userId, worldName, worldDescription);
            worldRepository.save(world);

            return worldDescription;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing AI response";
        }
    }
}
