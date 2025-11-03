package com.dominAItionBackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.dominAItionBackend.models.InviteMessage;

@Controller
public class InviteController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // When user A sends an invite
    @MessageMapping("/invite/send")
    public void sendInvite(InviteMessage invite) {
        // Send to the specific user’s topic
        messagingTemplate.convertAndSend(
            "/topic/invites/" + invite.getRecipientId(),
            invite
        );
    }
}
