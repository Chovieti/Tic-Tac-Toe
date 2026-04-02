package com.example.datasource.repository;

import com.example.datasource.model.DSUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<DSUser, UUID> {
    Optional<DSUser> findByLogin(String login);
}