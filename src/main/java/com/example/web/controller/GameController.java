package com.example.web.controller;

import com.example.domain.model.GameType;
import com.example.domain.service.GameService;
import com.example.web.mapper.MapperDomainWeb;
import com.example.web.dto.WebCurrentGame;
import com.example.web.dto.WebGameField;
import com.example.web.model.SecurityUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<WebCurrentGame> createNewGame(
            @AuthenticationPrincipal SecurityUserDetails user,
            @RequestParam(required = false) GameType gameType
    ) {
        WebCurrentGame response = MapperDomainWeb.toWebCurrentGame(
                service.createNewGame(user.getId(), gameType == null ? GameType.VS_COMPUTER : gameType)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<WebCurrentGame> getGame(@PathVariable UUID gameId) {
        WebCurrentGame response = MapperDomainWeb.toWebCurrentGame(service.getGame(gameId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    public ResponseEntity<List<WebCurrentGame>> getAvailableGames(
            @AuthenticationPrincipal SecurityUserDetails user
    ) {
        List<WebCurrentGame> response = service.getAvailableGames(user.getId())
                .stream()
                .map(MapperDomainWeb::toWebCurrentGame)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{gameId}/join")
    public ResponseEntity<WebCurrentGame> joinToGame(
            @PathVariable UUID gameId,
            @AuthenticationPrincipal SecurityUserDetails user
    ) {
        WebCurrentGame response = MapperDomainWeb.toWebCurrentGame(service.joinGame(gameId, user.getId()));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{gameId}")
    public ResponseEntity<WebCurrentGame> sendGame(@PathVariable UUID gameId,
                                                   @AuthenticationPrincipal SecurityUserDetails user,
                                                   @RequestBody WebGameField field) {
        WebCurrentGame response =
                MapperDomainWeb.toWebCurrentGame(service.processUserMove(gameId, user.getId(), field.field()));
        return ResponseEntity.ok(response);
    }
}
