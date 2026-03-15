package com.example.web.model;

import java.util.UUID;

public record WebUser(
        UUID userId,
        String login
) {}
