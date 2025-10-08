package com.dominAItionBackend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.ArrayList;

@Document(collection = "chats")
public class Chat {

    @Id
    private String id;

    private List<String> members;      // user IDs in the chat
    private List<String> messageIds;   // all message IDs in this chat

    public Chat() {
        this.members = new ArrayList<>();
        this.messageIds = new ArrayList<>();
    }

    public Chat(List<String> members) {
        this.members = members;
        this.messageIds = new ArrayList<>();
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<String> getMembers() { return members; }
    public void setMembers(List<String> members) { this.members = members; }

    public List<String> getMessageIds() { return messageIds; }
    public void setMessageIds(List<String> messageIds) { this.messageIds = messageIds; }
}
