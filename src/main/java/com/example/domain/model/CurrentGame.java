package com.example.domain.model;

import java.util.UUID;

public class CurrentGame {
    private final UUID id;
    private GameField field;

    public CurrentGame(UUID id, GameField field) {
        this.id = id;
        this.field = field;
    }

    public CurrentGame(GameField field) {
        id = UUID.randomUUID();
        this.field = field;
    }

    public UUID getId() {
        return id;
    }

    public GameField getField() {
        return field;
    }
}
