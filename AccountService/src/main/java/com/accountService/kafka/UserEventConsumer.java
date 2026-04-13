package com.accountService.kafka;

import com.accountService.kafka.event.UserCreatedEvent;
import com.accountService.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final AccountService accountService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user.created")
    public void onUserCreated(String message) {
        System.out.println("VERSAO NOVA AQUI");
        try {
            UserCreatedEvent event =
                    objectMapper.readValue(message, UserCreatedEvent.class);

            System.out.println("Evento recebido: " + event);

            accountService.createAccount(event.getUserId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}