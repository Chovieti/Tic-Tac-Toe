package com.example.web.dto;

public record JwtResponse(
        String type,
        String accessToken,
        String refreshToken
) {}
