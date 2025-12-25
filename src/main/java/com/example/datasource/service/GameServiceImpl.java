package com.example.datasource.service;

import com.example.datasource.repository.GameRepository;
import com.example.domain.exception.GameNotFoundException;
import com.example.domain.exception.GameOverException;
import com.example.domain.exception.InvalidMoveException;
import com.example.domain.model.CurrentGame;
import com.example.domain.model.GameField;
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
    public CurrentGame createNewGame() {
        GameField field = new GameField(new int[3][3]);
        CurrentGame newGame = new CurrentGame(field);
        repository.saveGame(newGame);
        return newGame;
    }

    @Override
    public CurrentGame getGame(UUID gameId) {
        Optional<CurrentGame> game = repository.getGame(gameId);
        if (game.isEmpty()) {
            throw new GameNotFoundException("Game not found: " + gameId);
        }
        return game.get();
    }

    @Override
    public Move nextTurn(UUID gameId) {
        Optional<CurrentGame> game = repository.getGame(gameId);
        if (game.isEmpty()) {
            throw new GameNotFoundException("Game not found: " + gameId);
        }
        int[][] field = game.get().getField().getField();
        if (checkWinner(field) != 0) {
            throw new GameOverException("Game is already over");
        }

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

    @Override
    public CurrentGame processUserMove(UUID gameId, int[][] userMoveField) {
        if (userMoveField == null || userMoveField.length != 3) {
            throw new InvalidMoveException("Field must be 3x3");
        }
        for (int i = 0; i < 3; i++) {
            if (userMoveField[i] == null || userMoveField[i].length != 3) {
                throw new InvalidMoveException("Field must be 3x3");
            }
        }

        Optional<CurrentGame> currentGame = repository.getGame(gameId);
        if (currentGame.isEmpty()) {
            throw new GameNotFoundException("Game not found: " + gameId);
        }
        CurrentGame game = currentGame.get();

        if (isGameOver(gameId)) {
            throw new GameOverException("Game is already over");
        }
        if (!validateField(gameId, userMoveField)) {
            throw new InvalidMoveException("Invalid move");
        }

        CurrentGame gameAfterUserMove = new CurrentGame(gameId, new GameField(userMoveField));
        repository.saveGame(gameAfterUserMove);
        if (!isGameOver(gameId)) {
            Move computerMove = nextTurn(gameId);
            if (computerMove == null) {
                throw new IllegalStateException("Computer cannot make a move");
            }
            int[][] computerField = applyMoveToField(userMoveField, computerMove);
            CurrentGame gameAfterComputerMove = new CurrentGame(gameId, new GameField(computerField));
            repository.saveGame(gameAfterComputerMove);

            return gameAfterComputerMove;
        }
        return gameAfterUserMove;
    }

    private int[][] applyMoveToField(int[][] field, Move move) {
        int[][] newField = new int[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(field[i], 0, newField[i], 0, 3);
        }
        newField[move.y()][move.x()] = move.value();
        return newField;
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
