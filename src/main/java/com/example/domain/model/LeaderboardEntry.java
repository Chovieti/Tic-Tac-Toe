package com.example.domain.model;

import java.util.UUID;

public record LeaderboardEntry(UUID userId, String login, double winRatio) {}
