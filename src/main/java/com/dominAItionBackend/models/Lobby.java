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

    @DBRef
    private List<User> users = new ArrayList<>(); // always initialized

    private String map = "default";

    // Default constructor
    public Lobby() {}

    public Lobby(String id, String map) {
        this.id = id;
        this.map = map;
    }

    // --- Getters & Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    public String getMap() { return map; }
    public void setMap(String map) { this.map = map; }

    // Add / remove user
    public void addUser(User user) {
        if (!users.contains(user)) users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }
}
