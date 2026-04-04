package com.example.web.controller;

import com.example.domain.model.GameType;
import com.example.domain.service.GameService;
import com.example.web.dto.LeaderboardResponse;
import com.example.web.mapper.MapperDomainWeb;
import com.example.web.dto.WebCurrentGame;
import com.example.web.dto.WebGameField;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<WebCurrentGame> createNewGame(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(required = false) GameType gameType
    ) {
        WebCurrentGame response = MapperDomainWeb.toWebCurrentGame(
                gameService.createNewGame(userId, gameType == null ? GameType.VS_COMPUTER : gameType)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<WebCurrentGame> getGame(@PathVariable UUID gameId) {
        WebCurrentGame response = MapperDomainWeb.toWebCurrentGame(gameService.getGame(gameId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    public ResponseEntity<List<WebCurrentGame>> getAvailableGames(
            @AuthenticationPrincipal UUID userId
    ) {
        List<WebCurrentGame> response = gameService.getAvailableGames(userId)
                .stream()
                .map(MapperDomainWeb::toWebCurrentGame)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/finished")
    public ResponseEntity<List<WebCurrentGame>> getFinishedGames(
            @AuthenticationPrincipal UUID userId
    ) {
        List<WebCurrentGame> games = gameService.getFinishedGames(userId)
                .stream()
                .map(MapperDomainWeb::toWebCurrentGame)
                .toList();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardResponse>> getLeaderboard(
            @RequestParam(defaultValue = "10") int limit
    ) {
        if (limit < 1) throw new IllegalArgumentException("Limit must be at least 1");
        List<LeaderboardResponse> response = gameService.getLeaderboard(limit)
                .stream()
                .map(entry -> new LeaderboardResponse(entry.userId(), entry.login(), entry.winRatio()))
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{gameId}/join")
    public ResponseEntity<WebCurrentGame> joinToGame(
            @PathVariable UUID gameId,
            @AuthenticationPrincipal UUID userId
    ) {
        WebCurrentGame response = MapperDomainWeb.toWebCurrentGame(gameService.joinGame(gameId, userId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{gameId}")
    public ResponseEntity<WebCurrentGame> sendGame(@PathVariable UUID gameId,
                                                   @AuthenticationPrincipal UUID userId,
                                                   @RequestBody WebGameField field) {
        WebCurrentGame response =
                MapperDomainWeb.toWebCurrentGame(gameService.processUserMove(gameId, userId, field.field()));
        return ResponseEntity.ok(response);
    }
}
