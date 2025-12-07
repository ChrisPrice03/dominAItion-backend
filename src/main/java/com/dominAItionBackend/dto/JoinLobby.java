package com.dominAItionBackend.dto;

public class JoinLobby {
    private String lobbyId;
    private String userId;
    private String characterId;

    // getters & setters
    public String getLobbyId() { return lobbyId; }
    public void setLobbyId(String lobbyId) { this.lobbyId = lobbyId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCharacterId() { return characterId; }
    public void setCharacterId(String characterId) { this.characterId = characterId; }
}
