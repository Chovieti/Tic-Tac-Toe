package com.example.domain.service;

import com.example.domain.model.*;

import java.util.List;
import java.util.UUID;

public interface GameService {
    // Метод для создания новой игры
    CurrentGame createNewGame(UUID creatorId, GameType type);
    // Метод для получения игры (возможно стоит сделать зависимость от userId?)
    CurrentGame getGame(UUID gameId);
    // Метод получения всех доступных игр
    List<CurrentGame> getAvailableGames(UUID userId);
    // Метод получения всех завершенных игр
    List<CurrentGame> getFinishedGames(UUID userId);
    // Метод получения таблицы лидеров
    List<LeaderboardEntry> getLeaderboard(int limit);
    // Метод присоединения к игре
    CurrentGame joinGame(UUID gameId, UUID userId);
    // Метод обработки хода пользователя (нужно расширить, чтобы смотрел на userId)
    CurrentGame processUserMove(UUID gameId, UUID userId, int[][] userMoveField);
    // Метод проверки корректности отправленного поля
    boolean validateField(CurrentGame game, int[][] newField, GameStatus status);
    // Метод проверки закончилась ли игра
    boolean isGameOver(CurrentGame game);
    // Метод отвечающий за ход компьютера
    CurrentGame makeComputerMove(CurrentGame gameAfterUserMove, GameStatus oldStatus);
    // Метод вычисляющий ход компьютера(включает в себя минимакс)
    Move nextTurn(CurrentGame game);
}
