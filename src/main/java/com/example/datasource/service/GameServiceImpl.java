package com.example.datasource.service;

import com.example.datasource.repository.GameRepository;
import com.example.domain.model.CurrentGame;
import com.example.domain.model.Move;
import com.example.domain.service.GameService;

import java.util.Optional;
import java.util.UUID;

public class GameServiceImpl implements GameService {

    private final GameRepository repository;

    public GameServiceImpl(GameRepository repository) {
        this.repository = repository;
    }
    @Override
    public Move nextTurn(UUID gameId) {
        Optional<CurrentGame> game = repository.getGame(gameId);
        if (game.isEmpty()) return null;
        int[][] field = game.get().getField().getField();
        if (checkWinner(field) != 0) return null;

        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[i][j] != 0) continue;
                field[i][j] = 2;
                int score = minimax(field, 0, false);
                field[i][j] = 0;
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = new Move(i, j, 2);
                }
            }
        }
        return bestMove;
    }

    private int minimax(int[][] field, int depth, boolean isMaximizing) {
        // Сначала проверка на победу
        int result = checkWinner(field);
        if (result == 1) return 20 - depth;
        if (result == -1) return depth - 20;

        if (fieldIsFilled(field)) return depth;

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (field[i][j] == 0) {
                        field[i][j] = 2;
                        int score = minimax(field, depth + 1, false);
                        field[i][j] = 0;
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (field[i][j] == 0) {
                        field[i][j] = 1;
                        int score = minimax(field, depth + 1, true);
                        field[i][j] = 0;
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }

    @Override
    public boolean validateField(UUID gameId, int[][] newField) {
        Optional<CurrentGame> game = repository.getGame(gameId);
        if (game.isEmpty()) return false;
        int[][] currentField = game.get().getField().getField();
        int changes = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (newField[i][j] != currentField[i][j]) {
                    if (currentField[i][j] != 0 || newField[i][j] != 1) return false;
                    changes++;
                }
            }
        }
        return changes == 1;
    }

    @Override
    public boolean isGameOver(UUID gameId) {
        Optional<CurrentGame> game = repository.getGame(gameId);
        if (game.isEmpty()) return true;
        int[][] field = game.get().getField().getField();
        if (checkWinner(field) != 0) return true;
        return fieldIsFilled(field);
    }

    private int checkWinner(int[][] field) {
        if (checkDiagonal(field)) return field[1][1] == 2 ? 1 : -1;
        for (int i = 0; i < 3; i++) {
            if (checkVertical(field, i)) return field[0][i] == 2 ? 1 : -1;
            if (checkHorizontal(field, i)) return field[i][0] == 2 ? 1 : -1;
        }
        return 0;
    }

    private boolean checkVertical(int[][] field, int col) {
        return (field[0][col] != 0 && (field[0][col] == field[1][col] && field[0][col] == field[2][col]));
    }

    private boolean checkHorizontal(int[][] field, int row) {
        return (field[row][0] != 0 && (field[row][0] == field[row][1] && field[row][0] == field[row][2]));
    }

    private boolean checkDiagonal(int[][] field) {
        return field[1][1] != 0 && ((field[1][1] == field[0][0] && field[1][1] == field[2][2]) ||
                (field[1][1] == field[0][2] && field[1][1] == field[2][0]));
    }

    private boolean fieldIsFilled(int [][] field) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[i][j] == 0) return false;
            }
        }
        return true;
    }
}
