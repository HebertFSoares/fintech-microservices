package com.authservice.kafka;

import com.authservice.kafka.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserCreated(UUID userId) {
        String topic = "user.created";
        String key = userId.toString();

        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(userId);
        kafkaTemplate.send(topic, key, userCreatedEvent);
    }

}
