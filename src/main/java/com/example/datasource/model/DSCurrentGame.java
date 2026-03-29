package com.example.datasource.model;

import com.example.domain.model.GameStatus;
import com.example.domain.model.GameType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "current_games")
public class DSCurrentGame {
    @Id
    private UUID id;
    @Embedded
    private DSGameField field;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_type", nullable = false)
    private GameType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_status", nullable = false)
    private GameStatus status;

    @Column(name = "player_x_id", nullable = false)
    private UUID playerXId;

    @Column(name = "player_o_id")
    private UUID playerOId;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;


    public DSCurrentGame() {}

    public DSCurrentGame(UUID id, DSGameField field, GameType type, GameStatus status, UUID playerXid, UUID playerOid, LocalDateTime creationDate) {
        this.id = id;
        this.field = field;
        this.type = type;
        this.status = status;
        this.playerXId = playerXid;
        this.playerOId = playerOid;
        this.creationDate = creationDate;
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public DSGameField getField() {
        return field;
    }
    public void setField(DSGameField field) {
        this.field = field;
    }
    public GameType getType() {
        return type;
    }
    public void setType(GameType type) {
        this.type = type;
    }
    public GameStatus getStatus() {
        return status;
    }
    public void setStatus(GameStatus status) {
        this.status = status;
    }
    public UUID getPlayerXId() {
        return playerXId;
    }
    public void setPlayerXId(UUID playerXid) {
        this.playerXId = playerXid;
    }
    public UUID getPlayerOId() {
        return playerOId;
    }
    public void setPlayerOId(UUID playerOid) {
        this.playerOId = playerOid;
    }
    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
}
