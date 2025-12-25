package com.example.web.model;

import com.example.domain.model.GameField;

import java.util.UUID;

public class WebCurrentGame {
    private final UUID id;
    private WebGameField field;

    public WebCurrentGame(UUID id, WebGameField field) {
        this.id = id;
        this.field = field;
    }

    public WebCurrentGame(WebGameField field) {
        id = UUID.randomUUID();
        this.field = field;
    }

    public UUID getId() {
        return id;
    }

    public WebGameField getField() {
        return field;
    }
}
