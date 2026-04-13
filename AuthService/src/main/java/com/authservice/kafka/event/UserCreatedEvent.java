package com.authservice.kafka.event;

import java.util.UUID;

public record UserCreatedEvent(UUID userId) {
}
