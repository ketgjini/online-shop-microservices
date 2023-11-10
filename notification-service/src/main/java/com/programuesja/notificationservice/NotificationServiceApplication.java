package com.programuesja.notificationservice;

import com.programuesja.notificationservice.service.SendEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
@ComponentScan(basePackages = {"com.programuesja.notificationservice.configuration",
                               "com.programuesja.notificationservice.service"})
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }


}
