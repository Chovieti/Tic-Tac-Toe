package com.example.domain.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User {
    private final UUID id;
    private final String login;
    private final String password;
    private final Set<Roles> roles;

    public User(UUID id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.roles = new HashSet<>();
    }

    public User(UUID id, String login, String password, Set<Roles> roles) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.roles = roles;
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

    public Set<Roles> getRoles() { return roles; }
}
