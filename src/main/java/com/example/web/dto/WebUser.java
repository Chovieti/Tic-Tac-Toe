package com.example.web.dto;

import java.util.UUID;

public record WebUser(
        UUID userId,
        String login
) {}
