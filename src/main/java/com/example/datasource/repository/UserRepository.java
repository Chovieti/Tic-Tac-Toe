package com.example.datasource.repository;

import com.example.datasource.model.DSUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<DSUser, UUID> {
    Optional<DSUser> findByLogin(String login);
}