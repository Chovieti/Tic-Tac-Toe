package com.example.domain.model;

import java.util.UUID;

public class User {
    private final UUID id;
    private final String login;
    private final String password;

    public User(UUID id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
