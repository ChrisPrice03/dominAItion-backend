package com.dominAItionBackend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "worlds")
public class World {

    @Id
    private String id;

    private String creatorID;       // ID of the user created the world
    private String worldName;         // the name of this world
    private String description;       // the description of this world


    public World() {}

    public World(String creatorID, String worldName, String description) {
        this.creatorID = creatorID;
        this.worldName = worldName;
        this.description = description;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCreatorID() { return creatorID; }
    public void setCreatorID(String creatorID) { this.creatorID = creatorID; }

    public String getWorldName() { return worldName; }
    public void setWorldName(String worldName) { this.worldName = worldName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
