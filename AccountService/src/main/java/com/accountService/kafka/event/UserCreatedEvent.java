package com.accountService.kafka.event;

import java.util.UUID;

public class UserCreatedEvent {

    private UUID userId;

    public UserCreatedEvent() {}

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}