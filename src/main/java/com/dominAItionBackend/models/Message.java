package com.dominAItionBackend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;


@Document(collection = "messages")
public class Message {

    @Id
    private String id;

    private String senderId;       // ID of the user who sent it
    private String chatId;         // the chat this message belongs to
    private String contents;       // message body text
    private LocalDateTime time;    // when it was sent

    private boolean isRead;         // whether the message has been read
    private String color;
    private String senderName;
    private int ignoreThis;

    public Message() {}

    public Message(String senderId, String chatId, String contents) {
        this.senderId = senderId;
        this.chatId = chatId;
        this.contents = contents;
        this.time = LocalDateTime.now();
        this.isRead = false;
    }

    public Message(String senderName, String color, String contents, String chatId) {
        this.senderName = senderName;
        this.color = color;
        this.contents = contents;
        this.chatId = chatId; // Same as the Game ID
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }

    public String getContents() { return contents; }
    public void setContents(String contents) { this.contents = contents; }

    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }

    @JsonProperty("isRead")   // ✅ Force JSON field name to match frontend
    public boolean isRead() {
        return isRead;
    }
    @JsonProperty("isRead")   // ✅ For deserialization too
    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public String getColor() {
        return color;
    }

    public String getSenderName() {
        return senderName;
    }


}
