package com.dominAItionBackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // --- existing verification email ---
    public void sendVerificationEmail(String to, String link) {
        String subject = "Verify Your DominAItion Account";
        String body = """
                Hello DominAItion player,

                Please verify your account by clicking the link below:

                %s

                This link will expire in 15 minutes.

                Thank you,
                The DominAItion Team
                """.formatted(link);

        sendEmail(to, subject, body);
    }

    // --- NEW password reset email ---
    public void sendPasswordResetEmail(String to, String resetLink) {
        String subject = "DominAItion Password Reset Request";
        String body = """
                Hello,

                We received a request to reset your DominAItion password.
                You can reset your password by clicking the link below:

                %s

                If you didn’t request this, please ignore this email.

                Best,
                The DominAItion Team
                """.formatted(resetLink);

        sendEmail(to, subject, body);
    }

    // --- NEW password change confirmation ---
    public void sendPasswordChangeConfirmation(String to) {
        String subject = "Your DominAItion Password Has Been Changed";
        String body = """
                Hello,

                Your DominAItion password has been successfully changed.
                If this wasn’t you, please contact support immediately.

                Stay safe,
                The DominAItion Team
                """;

        sendEmail(to, subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
