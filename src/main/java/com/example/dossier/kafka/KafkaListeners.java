package com.example.dossier.kafka;

import com.example.dossier.dto.EmailMessage;
import com.example.dossier.services.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;

@Component
public class KafkaListeners {


    final
    EmailService emailService;

    public KafkaListeners(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "finish-registration", groupId = "listeners1")
    void listener1(EmailMessage emailMessage) {

        emailService.sendFinishRegistration(emailMessage);
    }

    @KafkaListener(topics = "create-documents", groupId = "listeners1")
    void listener2(EmailMessage emailMessage) {

        emailService.sendCreateDocuments(emailMessage);
    }

    @KafkaListener(topics = "send-documents", groupId = "listeners1")
    void listener3(EmailMessage emailMessage) throws IOException, MessagingException {

        emailService.sendDocuments(emailMessage);

    }

    @KafkaListener(topics = "send-ses", groupId = "listeners1")
    void listener4(EmailMessage emailMessage) {
        emailService.sendSES(emailMessage);

    }

    @KafkaListener(topics = "credit-issued", groupId = "listeners1")
    void listener5(EmailMessage emailMessage) {
        emailService.congratulation(emailMessage);
    }

    @KafkaListener(topics = "application-denied", groupId = "listeners1")
    void listener6(EmailMessage emailMessage) {
        emailService.deniedCredit(emailMessage);
    }
}
