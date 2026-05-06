package br.com.autocarehub.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record User(UUID id, String username, String passwordHash, UserRole role, UUID customerId, boolean active, LocalDateTime createdAt) {

    public User {
        Objects.requireNonNull(id, "id is required");
        username = requireText(username, "Username is required");
        passwordHash = requireText(passwordHash, "Password hash is required");
        Objects.requireNonNull(role, "role is required");
        Objects.requireNonNull(createdAt, "createdAt is required");
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainException(message);
        }
        return value.trim();
    }
}
