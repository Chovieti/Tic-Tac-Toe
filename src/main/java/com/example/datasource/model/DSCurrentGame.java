package com.example.datasource.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "current_games")
public class DSCurrentGame {
    @Id
    private UUID id;
    @Embedded
    private DSGameField field;

    public DSCurrentGame() {}

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
