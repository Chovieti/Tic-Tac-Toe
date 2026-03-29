package com.example.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class CurrentGame {
    private final UUID id;
    private final GameField field;
    private final GameType type;
    private final GameStatus status;
    private final UUID playerXid;
    private final UUID playerOid;
    private final LocalDateTime creationDate;

    // Новые актуальные
    public CurrentGame(GameField field, GameType type, GameStatus status, UUID playerXid, UUID playerOid) {
        this.id = UUID.randomUUID();
        this.field = field;
        this.type = type;
        this.status = status;
        this.playerXid = playerXid;
        this.playerOid = playerOid;
        this.creationDate = LocalDateTime.now();
    }

    public CurrentGame(UUID id, GameField field, GameType type, GameStatus status, UUID playerXid, UUID playerOid, LocalDateTime creationDate) {
        this.id = id;
        this.field = field;
        this.type = type;
        this.status = status;
        this.playerXid = playerXid;
        this.playerOid = playerOid;
        this.creationDate = creationDate;
    }

    public CurrentGame withField(GameField newField) {
        return new CurrentGame(this.getId(), newField, this.type, this.status, this.playerXid, this.playerOid, this.creationDate);
    }

    public CurrentGame withStatus(GameStatus newStatus) {
        return new CurrentGame(this.getId(), this.field, this.type, newStatus, this.playerXid, this.playerOid, this.creationDate);
    }

    public CurrentGame withPlayerO(UUID playerOid) {
        return new CurrentGame(this.getId(), this.field, this.type, GameStatus.TURN_PLAYER_X, this.playerXid, playerOid, this.creationDate);
    }

    public UUID getId() {
        return id;
    }

    public GameField getField() {
        return field;
    }

    public GameType getType() {
        return type;
    }

    public GameStatus getStatus() {
        return status;
    }

    public UUID getPlayerXId() {
        return playerXid;
    }

    public UUID getPlayerOId() {
        return playerOid;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
}
