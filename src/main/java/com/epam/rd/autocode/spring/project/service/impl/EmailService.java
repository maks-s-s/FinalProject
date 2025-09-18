package com.epam.rd.autocode.spring.project.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordReset(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("bookstoretest996@gmail.com");
        message.setTo(to);
        message.setSubject("Password Reset");
        message.setText("Click this link to reset your password: " +
                "https://localhost:8084/reset-password?token=" + token);
        mailSender.send(message);
    }
}
