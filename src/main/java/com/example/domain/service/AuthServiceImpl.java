package com.example.domain.service;

import com.example.domain.exception.BadCredentialsException;
import com.example.domain.exception.UserAlreadyExistsException;
import com.example.domain.model.User;
import com.example.web.model.SignUpRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserService service;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserService service, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
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
}
