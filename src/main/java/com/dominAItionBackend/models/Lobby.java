package com.dominAItionBackend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "lobbies")
public class Lobby {

    @Id
    private String id;

    // Use DBRef to reference User documents in MongoDB
    @DBRef
    private List<User> users;

    private String map; // name or identifier of the map

    // Default constructor
    public Lobby() {
        this.users = new ArrayList<>();
        this.map = "default"; // optional default map
    }

    // Constructor with ID and map
    public Lobby(String id, String map) {
        this.id = id;
        this.map = map;
        this.users = new ArrayList<>();
    }

    // --- Getters & Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    public String getMap() { return map; }
    public void setMap(String map) { this.map = map; }

    // Add a User to the lobby (prevents duplicates)
    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
        }
    }

    // Remove a User from the lobby
    public void removeUser(User user) {
        users.remove(user);
    }
}
