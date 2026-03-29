package com.example.web.dto;

import com.example.domain.model.Roles;

import java.util.Set;
import java.util.UUID;

public record WebUser(
        UUID userId,
        String login,
        Set<Roles> roles
) {}
