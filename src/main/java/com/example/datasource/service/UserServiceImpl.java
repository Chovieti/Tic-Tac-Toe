package com.example.datasource.service;

import com.example.datasource.mapper.MapperDomainDatasource;
import com.example.datasource.model.DSUser;
import com.example.datasource.repository.UserRepository;
import com.example.domain.model.User;
import com.example.domain.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User registerUser(String login, String encodedPassword) {
        if (existsByLogin(login)) {
            throw new IllegalArgumentException("Login already taken");
        }
        DSUser newUser = new DSUser(UUID.randomUUID(), login, encodedPassword);
        repository.save(newUser);
        return MapperDomainDatasource.toDomainUser(newUser);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        Optional<DSUser> user = repository.findByLogin(login);
        if (user.isEmpty()) return Optional.empty();
        User u = MapperDomainDatasource.toDomainUser(user.get());
        return Optional.of(u);
    }

    @Override
    public boolean existsByLogin(String login) {
        return findByLogin(login).isPresent();
    }
}
