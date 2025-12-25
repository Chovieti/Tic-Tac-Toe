package com.example.domain.service;

import com.example.domain.model.CurrentGame;
import com.example.domain.model.Move;

import java.util.UUID;

public interface GameService {
    CurrentGame createNewGame();
    CurrentGame getGame(UUID gameId);
    Move nextTurn(UUID gameId);
    boolean validateField(UUID gameId, int[][] newField);
    boolean isGameOver(UUID gameId);
    CurrentGame processUserMove(UUID gameId, int[][] userMoveField);
}
