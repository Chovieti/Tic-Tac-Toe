package com.example.web.controller;

import com.example.domain.exception.BadCredentialsException;
import com.example.domain.service.AuthService;
import com.example.web.model.AuthResponse;
import com.example.web.model.SignUpRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody SignUpRequest request) {
        UUID userId = service.register(request);
        AuthResponse response = new AuthResponse(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        byte[] decodedBytes;
        try {
            decodedBytes = Base64.getDecoder().decode(authHeader.substring("Basic ".length()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String credentials = new String(decodedBytes, StandardCharsets.UTF_8);
        String[] values = credentials.split(":", 2);
        if (values.length != 2) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String login = values[0], password = values[1];
        try {
            UUID userId = service.authenticate(login, password);
            return ResponseEntity.ok(new AuthResponse(userId));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
