package com.starter.starter.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public String sendVerificationCode(String toEmail) {
        String code = generateCode();
        String subject = "Code de réinitialisation de mot de passe";
        String message = "Votre code de réinitialisation est : <b>" + code + "</b>";

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(message, true);
            mailSender.send(mimeMessage);
            return code; // Retourne le code pour validation côté backend
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi du mail", e);
        }
    }

    private String generateCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
