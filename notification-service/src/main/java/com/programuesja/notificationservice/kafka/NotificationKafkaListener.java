package com.programuesja.notificationservice.kafka;

import com.programuesja.notificationservice.service.SendEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
public class NotificationKafkaListener {

    private final SendEmail sendEmailService;

    public NotificationKafkaListener(SendEmail sendEmailService) {
        this.sendEmailService = sendEmailService;
    }

    @KafkaListener(topics = "notificationTopic")
    public void handleNotification(OrderPlacedEvent orderPlacedEvent) {
        sendEmailService.sendEmail(orderPlacedEvent.getOrderNumber());
        log.info("Your order {} was placed successfully!", orderPlacedEvent.getOrderNumber());
    }
}
