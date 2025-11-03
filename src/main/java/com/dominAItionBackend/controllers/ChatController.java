package com.dominAItionBackend.controllers;

import com.dominAItionBackend.models.Chat;
import com.dominAItionBackend.models.Message;
import com.dominAItionBackend.repository.ChatRepository;
import com.dominAItionBackend.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;     
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "https://dominaition-frontend.onrender.com"
})
public class ChatController {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    // ✅ Get all chats for a user
    @GetMapping("/{userId}")
    public List<Chat> getUserChats(@PathVariable String userId) {
        return chatRepository.findByMembersContaining(userId);
    }

    // ✅ Start new chat
    @PostMapping("/start")
    public Chat startChat(@RequestBody Map<String, Object> body) {
        List<String> members = (List<String>) body.get("members");

        // prevent duplicates
        List<Chat> existing = chatRepository.findByMembersContaining(members.get(0));
        for (Chat chat : existing) {
            if (chat.getMembers().containsAll(members) && chat.getMembers().size() == members.size()) {
                return chat;
            }
        }

        Chat newChat = new Chat(members);
        return chatRepository.save(newChat);
    }

    // Send message
    @PostMapping("/{chatId}/message")
    public Message sendMessage(@PathVariable String chatId, @RequestBody Message msg) {
        msg.setChatId(chatId);
        msg.setTime(LocalDateTime.now());
        msg.setRead(false); // new messages are unread by default
        Message saved = messageRepository.save(msg);

        Chat chat = chatRepository.findById(chatId).orElse(null);
        if (chat != null) {
            if (chat.getMessageIds() == null) chat.setMessageIds(new ArrayList<>());
            chat.getMessageIds().add(saved.getId());
            chatRepository.save(chat);
        }

        return saved;
    }

    // Get all messages in a chat
    @GetMapping("/{chatId}/messages")
    public List<Message> getMessages(@PathVariable String chatId) {
        return messageRepository.findByChatId(chatId);
    }

    // ✅ Mark all messages as read for a specific user
    @PutMapping("/{chatId}/mark-read/{userId}")
    public ResponseEntity<?> markMessagesAsRead(@PathVariable String chatId, @PathVariable String userId) {
        List<Message> msgs = messageRepository.findByChatId(chatId);

        msgs.stream()
            .filter(m -> !m.getSenderId().equals(userId) && !m.isRead())
            .forEach(m -> m.setRead(true));

        messageRepository.saveAll(msgs);
        return ResponseEntity.ok("Messages marked as read");
    }


}
