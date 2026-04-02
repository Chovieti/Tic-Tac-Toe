package com.example.datasource.model;

import com.example.domain.model.Roles;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class DSUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String login;
    @Column(nullable = false)
    private String password;
    @ElementCollection
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Roles> roles;

    public DSUser() {}

    public DSUser(String login, String password) {
        this.login = login;
        this.password = password;
        this.roles = new HashSet<>();
    }

    public DSUser(String login, String password, Set<Roles> roles) {
        this.login = login;
        this.password = password;
        this.roles = roles;
    }

    public DSUser(UUID id, String login, String password, Set<Roles> roles) {
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

    public Set<Roles> getRoles() {
        return roles;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
