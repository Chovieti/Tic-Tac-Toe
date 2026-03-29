package com.example.web.dto;

public record JwtRequest(
        String login,
        String password
) {}
