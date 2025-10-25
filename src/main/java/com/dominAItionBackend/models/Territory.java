package com.dominAItionBackend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "territories")
public class Territory {

    @Id
    private String id;
    private String gameId;        // ID of the game this territory belongs to

    private String ownerId;       // ID of the owner player
    private String territoryName;         // the name of this territory
    private int pointVal;       // the value of this territory in points


    public Territory() {}

    public Territory(String gameId, String ownerId, String territoryName, int pointVal) {
        this.gameId = gameId;
        this.territoryName = territoryName;
        this.pointVal = pointVal;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getGameID() { return gameId; }
    public void setGameID(String gameId) { this.gameId = gameId; }
    public String getOwnerID() { return ownerId; }
    public void setOwnerID(String ownerID) { this.ownerId = ownerID; }
    public String getTerritoryName() { return territoryName; }
    public void setTerritoryName(String territoryName) { this.territoryName = territoryName; }
    public int getPointVal() { return pointVal; }
    public void setPointVal(int pointVal) { this.pointVal = pointVal; }
}
