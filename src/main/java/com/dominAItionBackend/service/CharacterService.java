package com.dominAItionBackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dominAItionBackend.models.Character;
import com.dominAItionBackend.repository.CharacterRepository;

@Service
public class CharacterService {

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private AIService aiService;

    //function to call AI to generate character bio and save to database
    public String handleCharacterRequest(String userId, String input) {
        String response = aiService.callCharacterDefiningAgent(input);

        try {
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(response);

            String jsonified = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
            System.out.println("Full JSON:\n" + jsonified);

            String characterName = root.path("name").asText("No character name found");
            String characterBio = root.path("description").asText("No description found");
            int intelligence = root.path("stats").path("intelligence").asInt(0);
            int wisdom = root.path("stats").path("wisdom").asInt(0);
            int charisma = root.path("stats").path("charisma").asInt(0);
            int strength = root.path("stats").path("strength").asInt(0);
            int ingenuity = root.path("stats").path("ingenuity").asInt(0);

            //Save character to database
            Character character = new Character(userId, characterName, characterBio, intelligence, wisdom, charisma, strength, ingenuity);
            characterRepository.save(character);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing AI response";
        }
    }

    //}
}
