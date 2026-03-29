package com.example.domain.service;

import com.example.domain.exception.BadCredentialsException;
import com.example.domain.exception.UserAlreadyExistsException;
import com.example.domain.model.User;
import com.example.web.dto.JwtRequest;
import com.example.web.dto.JwtResponse;
import com.example.web.dto.SignUpRequest;
import com.example.web.model.JwtAuthentication;
import com.example.web.security.JwtProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserService service;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthServiceImpl(UserService service, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Override
    @Transactional
    public UUID register(SignUpRequest request) {
        String encoded = passwordEncoder.encode(request.password());
        if (request.login().isEmpty() || encoded.isEmpty()) {
            throw new BadCredentialsException("Login or password cannot be empty");
        }
        if (service.existsByLogin(request.login())) {
            throw new UserAlreadyExistsException("User with login " + request.login() + " already exists");
        }
        User newUser = service.registerUser(request.login(), encoded);
        return newUser.getId();
    }

    @Override
    public UUID authenticate(String login, String password) {
        User user = service.findByLogin(login)
                .orElseThrow(() -> new BadCredentialsException("Invalid login or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid login or password");
        }
        return user.getId();
    }

    @Override
    public JwtResponse authenticate(JwtRequest request) {
        User user = service.findByLogin(request.login())
                .orElseThrow(() -> new BadCredentialsException("Invalid login or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid login or password");
        }

        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRoles().stream().toList());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        return new JwtResponse("authenticate" ,accessToken, refreshToken);
    }

    @Override
    public JwtResponse refreshAccessToken(String refreshToken) {
        if (!jwtProvider.validateRefreshToken(refreshToken)) throw new BadCredentialsException("Invalid refresh token");

        var claims = jwtProvider.getClaims(refreshToken);
        UUID userId = UUID.fromString(claims.getSubject());
        User user = service.findById(userId);
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRoles().stream().toList());

        return new JwtResponse("refresh access", accessToken, refreshToken);
    }

    @Override
    public JwtResponse refreshRefreshToken(String refreshToken) {
        if (!jwtProvider.validateRefreshToken(refreshToken)) throw new BadCredentialsException("Invalid refresh token");

        var claims = jwtProvider.getClaims(refreshToken);
        UUID userId = UUID.fromString(claims.getSubject());
        User user = service.findById(userId);
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRoles().stream().toList());
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getId());

        return new JwtResponse("refresh access", accessToken, newRefreshToken);
    }

    @Override
    public JwtAuthentication getAuthentication() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthentication jwtAuthentication) {
            return jwtAuthentication;
        }
        return null;
    }
}
