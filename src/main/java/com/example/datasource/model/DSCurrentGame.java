package com.example.datasource.model;

import com.example.domain.model.GameStatus;
import com.example.domain.model.GameType;
import jakarta.persistence.*;

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


    public DSCurrentGame() {}

    public DSCurrentGame(UUID id, DSGameField field, GameType type, GameStatus status, UUID playerXid, UUID playerOid) {
        this.id = id;
        this.field = field;
        this.type = type;
        this.status = status;
        this.playerXId = playerXid;
        this.playerOId = playerOid;
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
}
