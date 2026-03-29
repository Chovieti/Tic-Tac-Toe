package com.example.domain.service;

import com.example.web.dto.JwtRequest;
import com.example.web.dto.JwtResponse;
import com.example.web.dto.SignUpRequest;
import com.example.web.model.JwtAuthentication;

import java.util.UUID;

public interface AuthService {
    UUID register(SignUpRequest request);
    UUID authenticate(String login, String password);
    JwtResponse authenticate(JwtRequest request);
    JwtResponse refreshAccessToken(String refreshToken);
    JwtResponse refreshRefreshToken(String refreshToken);
    JwtAuthentication getAuthentication();
}
