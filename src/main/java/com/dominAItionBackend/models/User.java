package com.dominAItionBackend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String username;
    private String email;
    private String password;
    private String icon; // base64 image or URL
    private String bio;

    private int wins;
    private int losses;
    private int gamesPlayed;
    private double totalPlayTime;

    private boolean emailVerified;
    private boolean musicEnabled;

    private Date time;

    private List<String> friendIds;
    private List<String> friendRequestIds;
    private List<String> friendSuggestionIds;
    private List<String> blockedIds;

    private boolean human;
    private boolean isPublic;
    private boolean online;

    private List<String> savedCharIds;
    private List<String> savedWorldIds;
    private List<String> savedGameIds;

    private List<String> chatIds;
    private List<String> achievementIds;

    

    // Default constructor with list initialization
    public User() {
        this.friendIds = new ArrayList<>();
        this.friendRequestIds = new ArrayList<>();
        this.friendSuggestionIds = new ArrayList<>();
        this.blockedIds = new ArrayList<>();
        this.savedCharIds = new ArrayList<>();
        this.savedWorldIds = new ArrayList<>();
        this.savedGameIds = new ArrayList<>();
        this.chatIds = new ArrayList<>();
        this.achievementIds = new ArrayList<>();

        this.time = new Date();
        this.wins = 0;
        this.losses = 0;
        this.totalPlayTime = 0;
        this.gamesPlayed = 0;
        this.human = false;
        this.isPublic = true;
        this.online = false;
        this.emailVerified = false;
        this.musicEnabled = true;
    }

    // Constructor for quick creation
    public User(String username, String email, String password) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
        this.emailVerified = false;
        this.musicEnabled = true;
    }

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }

    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }

    public double getTotalPlayTime() { return totalPlayTime; }
    public void setTotalPlayTime(double totalPlayTime) { this.totalPlayTime = totalPlayTime; }

    public int getGamesPlayed() { return gamesPlayed; }
    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }

    public boolean isEmailVerified() { return emailVerified; }       
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public boolean isMusicEnabled() { return musicEnabled; }
    public void setMusicEnabled(boolean musicEnabled) { this.musicEnabled = musicEnabled; }

    public Date getTime() { return time; }
    public void setTime(Date time) { this.time = time; }

    public List<String> getFriendIds() { return friendIds; }
    public void setFriendIds(List<String> friendIds) { this.friendIds = friendIds; }

    public List<String> getFriendRequestIds() { return friendRequestIds; }
    public void setFriendRequestIds(List<String> friendRequestIds) { this.friendRequestIds = friendRequestIds; }

    public List<String> getFriendSuggestionIds() { return friendSuggestionIds; }
    public void setFriendSuggestionIds(List<String> friendSuggestionIds) { this.friendSuggestionIds = friendSuggestionIds; }

    public List<String> getBlockedIds() { return blockedIds; }
    public void setBlockedIds(List<String> blockedIds) { this.blockedIds = blockedIds; }

    public boolean isHuman() { return human; }
    public void setHuman(boolean human) { this.human = human; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }

    public boolean isOnline() { return online; }
    public void setOnline(boolean online) { this.online = online; }

    public List<String> getSavedCharIds() { return savedCharIds; }
    public void setSavedCharIds(List<String> savedCharIds) { this.savedCharIds = savedCharIds; }

    public List<String> getSavedWorldIds() { return savedWorldIds; }
    public void setSavedWorldIds(List<String> savedWorldIds) { this.savedWorldIds = savedWorldIds; }

    public List<String> getSavedGameIds() { return savedGameIds; }
    public void setSavedGameIds(List<String> savedGameIds) { this.savedGameIds = savedGameIds; }

    public List<String> getChatIds() { return chatIds; }
    public void setChatIds(List<String> chatIds) { this.chatIds = chatIds; }

    public List<String> getAchievementIds() { return achievementIds; }
    public void setAchievementIds(List<String> achievementIds) { this.achievementIds = achievementIds; }
}
