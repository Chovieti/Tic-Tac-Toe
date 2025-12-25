package com.example.datasource.repository;

import com.example.datasource.mapper.MapperDomainDatasource;
import com.example.datasource.model.DSCurrentGame;
import com.example.datasource.storage.GameStorage;
import com.example.domain.model.CurrentGame;

import java.util.Optional;
import java.util.UUID;

public class GameRepository {
    private final GameStorage storage;

    public GameRepository(GameStorage storage) {
        this.storage = storage;
    }

    public void saveGame(CurrentGame game) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null");
        }
        DSCurrentGame dsGame = MapperDomainDatasource.toDSCurrentGame(game);
        storage.saveGame(dsGame);
    }

    public Optional<CurrentGame> getGame(UUID id) {
        return storage.getGame(id)
                .map(MapperDomainDatasource::toDomainCurrentGame);
    }
}
