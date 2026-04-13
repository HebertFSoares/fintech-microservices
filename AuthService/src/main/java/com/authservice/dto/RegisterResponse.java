package com.authservice.dto;

import com.authservice.enums.UserRole;

import java.util.UUID;

public record RegisterResponse(UUID id, String name, String email, UserRole role) {
}
