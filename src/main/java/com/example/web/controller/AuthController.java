package com.example.web.controller;

import com.example.domain.service.AuthService;
import com.example.web.dto.JwtRequest;
import com.example.web.dto.JwtResponse;
import com.example.web.model.AuthResponse;
import com.example.web.dto.SignUpRequest;
import com.example.web.model.JwtAuthentication;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/authorization")
    public ResponseEntity<JwtResponse> authorization(@RequestBody JwtRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody String refreshToken) {
        return ResponseEntity.ok(service.refreshAccessToken(refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody String refreshToken) {
        return ResponseEntity.ok(service.refreshRefreshToken(refreshToken));
    }

    @GetMapping("/me")
    public ResponseEntity<JwtAuthentication> getMe() {
        return ResponseEntity.ok(service.getAuthentication());
    }
}
