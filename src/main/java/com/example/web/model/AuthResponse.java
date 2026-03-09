package com.example.web.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuthResponse {
    private final UUID userId;
    private final LocalDateTime timestamp;

    public AuthResponse(UUID userId) {
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }

    public UUID getUserId() {
        return userId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
