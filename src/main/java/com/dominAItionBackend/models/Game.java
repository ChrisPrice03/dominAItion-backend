package com.dominAItionBackend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

import java.util.*;

@Document(collection = "games")
public class Game {

    @Id
    private String id;

    private List<String> playerIds; // IDs of players in the game
    private List<String> spectatorIds; // IDs of users spectating the game
    private Map<String, String> characterIds; // maps to playerIds by index
    private String worldId; // the world this game is played in
    private List<String> territoryIds; // IDs of territories involved in the game

    private Map<String, Integer> playerPoints; // maps player ID to their points
    private int winningPoints; // points needed to win
    private String game_log; // log of game events
    private LocalDateTime time; // when the game was created
    private String status; // current status of the game (e.g., "ongoing", "completed")
    private boolean isPublic; // whether the game is public or private
    private int joinCode; // code to join the game
    private String joinURL; // URL to join the game
    private String chatId; // chat associated with the game
    private String summary; // brief summary of the game
    private int turn;
    private String storyboard;
    private List<Message> chat;
    private List<Message> gameLog;
    private String winnerId;

    // Default constructor with list initialization
    public Game() {
        this.playerIds = new ArrayList<>();
        this.spectatorIds = new ArrayList<>();
        this.characterIds = new HashMap<String, String>();
        this.worldId = "";
        this.territoryIds = new ArrayList<>();
        this.playerPoints = new HashMap<String, Integer>();
        this.winningPoints = 0;
        this.game_log = "";
        this.time = LocalDateTime.now();
        this.status = "initializing";
        this.isPublic = true;
        this.joinCode = 0;
        this.joinURL = "";
        this.chatId = "";
        this.turn = 0;
        this.storyboard = "";
        this.chat = new ArrayList<>();
        this.gameLog = new ArrayList<>();
        this.winnerId = "";
    }

    // Constructor for quick creation
    public Game(String worldId, int winningPoints) {
        this();
        this.worldId = worldId;
        this.winningPoints = winningPoints;
    }

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<String> getPlayerIds() { return playerIds; }
    public void setPlayerIds(List<String> playerIds) { this.playerIds = playerIds; }
    public void addPlayerId(String playerId) { this.playerIds.add(playerId); }
    public void removePlayerId(String playerId) { this.playerIds.remove(playerId); }
    public List<String> getSpectatorIds() { return spectatorIds; }
    public void setSpectatorIds(List<String> spectatorIds) { this.spectatorIds = spectatorIds; }
    public void addSpectatorId(String spectatorId) { this.spectatorIds.add(spectatorId); }
    public void removeSpectatorId(String spectatorId) { this.spectatorIds.remove(spectatorId); }
    public Map<String, String> getCharacterIds() { return characterIds; }
    public void setCharacterIds(Map<String, String> characterIds) { this.characterIds = characterIds; }
    public void addCharacterId(String playerId, String characterId) { this.characterIds.put(playerId, characterId); }
    public void removeCharacterId(String playerId) { this.characterIds.remove(playerId); }
    public String getWorldId() { return worldId; }
    public void setWorldId(String worldId) { this.worldId = worldId; }
    public List<String> getTerritoryIds() { return territoryIds; }
    public void setTerritoryIds(List<String> territoryIds) { this.territoryIds = territoryIds; }
    public Map<String, Integer> getPlayerPoints() { return playerPoints; }
    public void setPlayerPoints(Map<String, Integer> playerPoints) { this.playerPoints = playerPoints; }
    public void setPlayerPointsForPlayer(String playerId, int points) { this.playerPoints.put(playerId, points); }
    public int getWinningPoints() { return winningPoints; }
    public void setWinningPoints(int winningPoints) { this.winningPoints = winningPoints; }
    public String getGame_log() { return game_log; }
    public void setGame_log(String game_log) { this.game_log = game_log; }
    public void addToGameLog(String logEntry) {
        this.game_log += LocalDateTime.now().toString() + ": " + logEntry + "\n";
    }
    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }
    public int getJoinCode() { return joinCode; }
    public void setJoinCode(int joinCode) { this.joinCode = joinCode; }
    public String getJoinURL() { return joinURL; }
    public void setJoinURL(String joinURL) { this.joinURL = joinURL; }
    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public int getTurn() { return turn; }
    public void setTurn(int turn) { this.turn = turn; }
    public void setStoryboard(String storyboard) { this.storyboard = storyboard; }
    public String getStoryboard() { return storyboard; }
    public List<Message> getChat() {
        return chat;
    }
    public void setChat(List<Message> chat) {
        this.chat = chat;
    }
    public List<Message> getGameLog() {
        return gameLog;
    }
    public void setGameLog(List<Message> gameLog) {
        this.gameLog = gameLog;
    }
    public String getWinnerId() {
        return winnerId;
    }
    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }
}
