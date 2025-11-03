package com.dominAItionBackend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "characters")
public class Character {

    @Id
    private String id;

    private String creatorID;
    private String characterName;
    private String characterBio;  

    private int wins;
    private int losses;
    private int gamesPlayed;

    private boolean isPublic;

    public Character() {}

    public Character(String creatorID, String characterName, String characterBio)
    {
        this();
        this.creatorID = creatorID;
        this.characterName = characterName;
        this.characterBio = characterBio;
        
        this.isPublic = false;
        this.wins = 0;
        this.losses = 0;
        this.gamesPlayed = 0;
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

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
