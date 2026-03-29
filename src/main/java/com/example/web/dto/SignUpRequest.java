package com.example.web.dto;

import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
        @NotBlank(message = "Login cannot be empty")
        String login,
        @NotBlank(message = "Password cannot be empty")
        String password
) {}
