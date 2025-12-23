package com.example.datasource.storage;

import com.example.datasource.model.DSCurrentGame;

import java.util.Optional;
import java.util.UUID;

public interface GameStorage {
    Optional<DSCurrentGame> getGame(UUID id);
    void saveGame(DSCurrentGame game);
    void deleteGame(UUID id);
}
