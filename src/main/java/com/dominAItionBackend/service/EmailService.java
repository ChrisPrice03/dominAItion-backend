package com.dominAItionBackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;

import java.io.File;

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

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); 
        mailSender.send(message);
    }

    public void sendEmailWithInlineImage(String to, String subject, String htmlBody, File imageFile)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        if (imageFile != null && imageFile.exists()) {
            // "screenshotImage" is the Content-ID (cid) used in HTML
            helper.addInline("screenshotImage", imageFile);
        }

        mailSender.send(message);
    }
}
