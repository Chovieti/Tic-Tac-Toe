package com.example.domain.service;

import com.example.datasource.repository.GameRepository;
import com.example.domain.exception.GameNotFoundException;
import com.example.domain.exception.GameNotJoinableException;
import com.example.domain.exception.GameOverException;
import com.example.domain.exception.InvalidMoveException;
import com.example.domain.model.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GameServiceImpl implements GameService {
    private static int EMPTY_CELL = 0, PLAYER_X = 1, PLAYER_O = 2;

    private final GameRepository repository;

    public GameServiceImpl(GameRepository repository) {
        this.repository = repository;
    }

    @Override
    public CurrentGame createNewGame(UUID creatorId, GameType type) {
        GameField field = new GameField(new int[3][3]);
        GameStatus status = (type == GameType.VS_COMPUTER) ? GameStatus.TURN_PLAYER_X : GameStatus.WAITING;
        CurrentGame newGame = new CurrentGame(field, type, status, creatorId, null);
        repository.saveGame(newGame);
        return newGame;
    }

    @Override
    public CurrentGame getGame(UUID gameId) {
        Optional<CurrentGame> opt = repository.getGame(gameId);
        if (opt.isEmpty()) {
            throw new GameNotFoundException("Game not found: " + gameId);
        }
        return opt.get();
    }

    @Override
    public List<CurrentGame> getAvailableGames(UUID userId) {
        return repository.findAvailableGames(userId);
    }

    @Override
    public CurrentGame joinGame(UUID gameId, UUID userId) {
        Optional<CurrentGame> opt = repository.getGame(gameId);
        if (opt.isEmpty()) {
            throw new GameNotFoundException("Game not found: " + gameId);
        }
        CurrentGame game = opt.get();
        if (game.getPlayerXId().equals(userId)) {
            throw  new GameNotJoinableException("You already in this game");
        }
        if (game.getType() == GameType.VS_COMPUTER) {
            throw new GameNotJoinableException("This game vs computer");
        }
        if (game.getType() == GameType.VS_PLAYER && game.getStatus() != GameStatus.WAITING) {
            throw new GameNotJoinableException("Game already started");
        }
        CurrentGame updatedGame = game.withPlayerO(userId);
        repository.saveGame(updatedGame);
        return updatedGame;
    }

    @Override
    public CurrentGame processUserMove(UUID gameId, UUID userId, int[][] userMoveField) {
        Optional<CurrentGame> opt = repository.getGame(gameId);
        if (opt.isEmpty()) {
            throw new GameNotFoundException("Game not found: " + gameId);
        }
        CurrentGame game = opt.get();
        if (isGameOver(game)) {
            throw new GameOverException("Game is already over");
        }
        boolean playerX = game.getPlayerXId().equals(userId);
        boolean playerO = game.getPlayerOId() != null && game.getPlayerOId().equals(userId);
        if (!playerX && !playerO) {
            throw new InvalidMoveException("This is not your game");
        }
        if ((playerX && game.getStatus() != GameStatus.TURN_PLAYER_X)
        || (playerO && game.getStatus() != GameStatus.TURN_PLAYER_O)) {
            throw new InvalidMoveException("This is not your turn");
        }

        if (userMoveField == null || userMoveField.length != 3) {
            throw new InvalidMoveException("Field must be 3x3");
        }
        for (int i = 0; i < 3; i++) {
            if (userMoveField[i] == null || userMoveField[i].length != 3) {
                throw new InvalidMoveException("Field must be 3x3");
            }
        }

        GameStatus oldStatus = game.getStatus();
        if (!validateField(game, userMoveField, oldStatus)) {
            throw new InvalidMoveException("Incorrect field");
        }
        GameStatus newStatus = switch (oldStatus) {
            case TURN_PLAYER_X -> GameStatus.TURN_PLAYER_O;
            case TURN_PLAYER_O -> GameStatus.TURN_PLAYER_X;
            default -> GameStatus.DRAW;
        };
        CurrentGame gameAfterUserMove = game.withField(new GameField(userMoveField)).withStatus(newStatus);
        gameAfterUserMove = determineGameStatus(gameAfterUserMove);
        repository.saveGame(gameAfterUserMove);
        if (isGameOver(gameAfterUserMove) || game.getType() != GameType.VS_COMPUTER) {
            return gameAfterUserMove;
        } else {
            return makeComputerMove(gameAfterUserMove, oldStatus);
        }
    }

    // Оставить GameStatus на случай если можно будет изменить кто чем ходит
    @Override
    public CurrentGame makeComputerMove(CurrentGame gameAfterUserMove, GameStatus oldStatus) {
        Move computerMove = nextTurn(gameAfterUserMove);
        if (computerMove == null) {
            throw new IllegalStateException("Computer cannot make a move");
        }
        int[][] computerField = applyMoveToField(gameAfterUserMove.getField().getField(), computerMove);
        CurrentGame gameAfterComputerMove =
                gameAfterUserMove
                .withField(new GameField(computerField))
                .withStatus(oldStatus);
        gameAfterComputerMove = determineGameStatus(gameAfterComputerMove);
        repository.saveGame(gameAfterComputerMove);
        return gameAfterComputerMove;
    }

    @Override
    public boolean validateField(CurrentGame game, int[][] newField, GameStatus status) {
        int[][] currentField = game.getField().getField();
        int changes = 0;
        int whichPlayerTurn = switch (status) {
            case TURN_PLAYER_X -> PLAYER_X;
            case TURN_PLAYER_O -> PLAYER_O;
            default -> 0;
        };
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (newField[i][j] != currentField[i][j]) {
                    // Если игрок поставил не свое значение
                    if (newField[i][j] != whichPlayerTurn) return false;
                    // Если игрок изменил уже занятое значение
                    if (currentField[i][j] != EMPTY_CELL) return false;
                    changes++;
                }
            }
        }
        return changes == 1;
    }

    @Override
    public boolean isGameOver(CurrentGame game) {
        return game.getStatus() == GameStatus.WIN_PLAYER_X
                || game.getStatus() == GameStatus.WIN_PLAYER_O
                || game.getStatus() == GameStatus.DRAW;
    }

    private CurrentGame determineGameStatus(CurrentGame game) {
        return switch (checkWinner(game.getField().getField())) {
            default -> fieldIsFilled(game.getField().getField()) ? game.withStatus(GameStatus.DRAW) : game;
            case 1 -> game.withStatus(GameStatus.WIN_PLAYER_X);
            case -1 -> game.withStatus(GameStatus.WIN_PLAYER_O);
        };
    }
    @Override
    public Move nextTurn(CurrentGame game) {
        int[][] field = game.getField().getField();
        if (checkWinner(field) != 0) {
            throw new GameOverException("Game is already over");
        }

        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[i][j] != EMPTY_CELL) continue;
                field[i][j] = PLAYER_O;
                int score = minimax(field, 0, false);
                field[i][j] = EMPTY_CELL;
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = new Move(i, j, PLAYER_O);
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
                    if (field[i][j] == EMPTY_CELL) {
                        field[i][j] = PLAYER_O;
                        int score = minimax(field, depth + 1, false);
                        field[i][j] = EMPTY_CELL;
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (field[i][j] == EMPTY_CELL) {
                        field[i][j] = PLAYER_X;
                        int score = minimax(field, depth + 1, true);
                        field[i][j] = EMPTY_CELL;
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
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
        if (checkDiagonal(field)) return field[1][1] == PLAYER_O ? -1 : 1;
        for (int i = 0; i < 3; i++) {
            if (checkVertical(field, i)) return field[0][i] == PLAYER_O ? -1 : 1;
            if (checkHorizontal(field, i)) return field[i][0] == PLAYER_O ? -1 : 1;
        }
        return 0;
    }

    private boolean checkVertical(int[][] field, int col) {
        return (field[0][col] != EMPTY_CELL && (field[0][col] == field[1][col] && field[0][col] == field[2][col]));
    }

    private boolean checkHorizontal(int[][] field, int row) {
        return (field[row][0] != EMPTY_CELL && (field[row][0] == field[row][1] && field[row][0] == field[row][2]));
    }

    private boolean checkDiagonal(int[][] field) {
        return field[1][1] != EMPTY_CELL && ((field[1][1] == field[0][0] && field[1][1] == field[2][2]) ||
                (field[1][1] == field[0][2] && field[1][1] == field[2][0]));
    }

    private boolean fieldIsFilled(int [][] field) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[i][j] == EMPTY_CELL) return false;
            }
        }
        return true;
    }
}
