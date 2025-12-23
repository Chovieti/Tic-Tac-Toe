package com.example.datasource.model;

import java.util.UUID;

public class DSCurrentGame {
    private final UUID id;
    private DSGameField field;

    public DSCurrentGame(UUID id, DSGameField field) {
        this.id = id;
        this.field = field;
    }

    public DSCurrentGame(DSGameField field) {
        id = UUID.randomUUID();
        this.field = field;
    }

    public UUID getId() {
        return id;
    }

    public DSGameField getField() {
        return field;
    }
}
