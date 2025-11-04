package com.dominAItionBackend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "lobbies")
public class Lobby {

    @Id
    private String id;

    @DBRef
    private List<User> users = new ArrayList<>();

    private String map = "default";
    private String code;

    @Field("isPrivate") // store in MongoDB as "isPrivate"
    private boolean privateLobby;

    // Default constructor
    public Lobby() {}

    // Full constructor
    public Lobby(String id, List<User> users, String map, String code, boolean privateLobby) {
        this.id = id;
        this.users = users != null ? users : new ArrayList<>();
        this.map = map != null ? map : "default";
        this.code = code;
        this.privateLobby = privateLobby;
    }

    // --- Getters & Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    public String getMap() { return map; }
    public void setMap(String map) { this.map = map; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public boolean isPrivateLobby() { return privateLobby; }
    public void setPrivateLobby(boolean privateLobby) { this.privateLobby = privateLobby; }

    // Add / remove user
    public void addUser(User user) {
        if (!users.contains(user)) users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }
}
