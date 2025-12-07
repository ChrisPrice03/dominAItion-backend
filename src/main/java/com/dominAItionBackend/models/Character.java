package com.dominAItionBackend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "characters")
public class Character {

    @Id
    private String id;

    private String creatorID; // ID of the user created the character
    private String characterName;  // the name of this character
    private String characterBio;    // the bio/description of this character

    private int intelligence;   // intelligence stat
    private int wisdom;       // wisdom stat
    private int charisma;     // charisma stat
    private int strength;     // strength stat
    private int ingenuity;    // ingenuity stat


    public Character() {}

    public Character(String creatorID, String characterName, String characterBio, int intelligence, int wisdom, int charisma, int strength, int ingenuity)
    {
        this();
        this.creatorID = creatorID;
        this.characterName = characterName;
        this.characterBio = characterBio;
        
        this.intelligence = intelligence;
        this.wisdom = wisdom;
        this.charisma = charisma;
        this.strength = strength;
        this.ingenuity = ingenuity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getCharacterBio() {
        return characterBio;
    }

    public void setCharacterBio(String characterBio) {
        this.characterBio = characterBio;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getWisdom() {
        return wisdom;
    }

    public void setWisdom(int wisdom) {
        this.wisdom = wisdom;
    }

    public int getCharisma() {
        return charisma;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getIngenuity() {
        return ingenuity;
    }

    public void setIngenuity(int ingenuity) {
        this.ingenuity = ingenuity;
    }
}
