package com.programuesja.notificationservice.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public record SendEmail(JavaMailSender javaMailSender) {

    public void sendEmail(String orderNumber) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("email@gmail.com");
        msg.setTo("test@gmail.com");
        msg.setSubject("Message from Kafka");
        msg.setText("Your order " + orderNumber + " was placed successfully!");

        javaMailSender.send(msg);
    }
}
