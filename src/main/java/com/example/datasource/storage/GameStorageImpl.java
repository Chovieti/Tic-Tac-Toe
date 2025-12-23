package com.example.datasource.storage;

import com.example.datasource.model.DSCurrentGame;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameStorageImpl implements GameStorage {
    private final ConcurrentHashMap<UUID, DSCurrentGame> games;

    public GameStorageImpl() {
        this.games = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<DSCurrentGame> getGame(UUID id) {
        return Optional.ofNullable(games.get(id));
    }

    @Override
    public void saveGame(DSCurrentGame game) {
        games.put(game.getId(), game);
    }

    @Override
    public void deleteGame(UUID id) {
        games.remove(id);
    }
}
