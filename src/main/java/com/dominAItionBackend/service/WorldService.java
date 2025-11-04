package com.dominAItionBackend.service;

import com.dominAItionBackend.models.Territory;
import com.dominAItionBackend.models.World;
import com.dominAItionBackend.repository.TerritoryRepository;
import com.dominAItionBackend.repository.WorldRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorldService {
    //this class will manage world creation

    @Autowired
    private AIService aiService;

    @Autowired
    private WorldRepository worldRepository;

    @Autowired
    TerritoryRepository territoryRepository;

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

    public List<String> generateTerritories(String worldID) {

        //get the world description from the database
        World world = worldRepository.findById(worldID).orElse(null);
        String worldDescription = world != null ? world.getDescription() : "";
        
        System.out.println("world: " + world);
        System.out.println("worldDesc: " + worldDescription);

        //call AI service to generate territories for the world
        String response = aiService.callTerritoryGeneratingAgent(worldDescription);
        
        try {
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(response);

            List<String> territoryIds = new ArrayList<>();

//            String jsonified = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
//            System.out.println("Full JSON:\n" + jsonified);

            // Iterate over the JSON fields
            root.fields().forEachRemaining(entry -> {
                String territoryName = entry.getKey();
                int pointVal = entry.getValue().asInt();

                // Create a new Territory object
                Territory territory = new Territory(worldID, null, territoryName, pointVal);

                // Save the territory to the database
                Territory savedTerritory = territoryRepository.save(territory);

                // Add the ID of the saved territory to the list
                territoryIds.add(savedTerritory.getId());
            });

            System.out.println("ITS HERE");

            return territoryIds;
        } catch (Exception e) {
            System.out.println("HERE IS THE ERROR:");
            e.printStackTrace();
            return List.of();
        }
    }
}
