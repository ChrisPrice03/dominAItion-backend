package com.dominAItionBackend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "lobbies")
public class Lobby {

    @Id
    private String id;

    @DBRef
    private List<User> users = new ArrayList<>();

    private String map = "default";
    private String code;
    private boolean single;

    @Field("isPrivate") // store in MongoDB as "isPrivate"
    private boolean privateLobby;

    private Map<String, String> userCharacterMap = new HashMap<>();

    // Optionally store the gameId once created
    private String gameId;

    // Default constructor
    public Lobby() {}

    // Full constructor
    public Lobby(String id, List<User> users, String map, String code, boolean privateLobby, boolean single) {
        this.id = id;
        this.users = users != null ? users : new ArrayList<>();
        this.map = map != null ? map : "default";
        this.code = code;
        this.privateLobby = privateLobby;
        this.single = single;
    }

    // --- Getters & Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public boolean getSingle() { return single; }
    public void setSingle(boolean single) { this.single = single; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    public String getMap() { return map; }
    public void setMap(String map) { this.map = map; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public boolean isPrivateLobby() { return privateLobby; }
    public void setPrivateLobby(boolean privateLobby) { this.privateLobby = privateLobby; }

    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }

    public Map<String, String> getUserCharacterMap() {
        return userCharacterMap;
    }
    public void setUserCharacterMap(Map<String, String> userCharacterMap) {
        this.userCharacterMap = userCharacterMap;
    }

    // Add / remove user
    public void addUser(User user) {
        if (!users.contains(user)) users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public void addUserWithCharacter(User user, String characterId) {
        this.addUser(user); // your existing logic to add to users list
        if (characterId != null && !characterId.isBlank()) {
            userCharacterMap.put(user.getId(), characterId);
        }
    }

    public String getCharacterIdForUser(String userId) {
        return userCharacterMap.get(userId);
    }
}
