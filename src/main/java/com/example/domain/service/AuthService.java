package com.example.domain.service;

import com.example.web.dto.SignUpRequest;

import java.util.UUID;

public interface AuthService {
    UUID register(SignUpRequest request);
    UUID authenticate(String login, String password);
}
