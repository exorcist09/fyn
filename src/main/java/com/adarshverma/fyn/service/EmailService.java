package com.adarshverma.fyn.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    //    loading from application.properties
    @Value("${spring.mail.properties,mail.smtp.from}")
    private String fromEmail;


    // Implementation for sending email
    public void sendEmail(String to, String subject, String body) {
        try {
//            use of SimpleMailMessage Class
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to); // this to is from method parameter of class SimpleMailMessage
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

}
