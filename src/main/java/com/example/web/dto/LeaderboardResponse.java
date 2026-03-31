package com.example.web.dto;

import java.util.UUID;

public record LeaderboardResponse(UUID userId, String login, double winRatio) {}
