package com.example.domain.service;

import com.example.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User registerUser(String login, String encodedPassword);
    Optional<User> findByLogin(String login);
    boolean existsByLogin(String login);
    User findById(UUID id);
}
