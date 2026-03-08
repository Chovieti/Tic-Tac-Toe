package com.example.datasource.repository;

import com.example.datasource.model.DSCurrentGame;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CurrentGameDataRepository extends CrudRepository<DSCurrentGame, UUID> {}
