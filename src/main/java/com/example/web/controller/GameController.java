package com.example.web.controller;

import com.example.domain.service.GameService;
import com.example.web.mapper.MapperDomainWeb;
import com.example.web.model.WebCurrentGame;
import com.example.web.model.WebGameField;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class GameController {
    private final GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    @GetMapping("/game")
    public ResponseEntity<WebCurrentGame> createNewGame() {
        WebCurrentGame response = MapperDomainWeb.toWebCurrentGame(service.createNewGame());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<WebCurrentGame> getGame(@PathVariable UUID gameId) {
        WebCurrentGame response = MapperDomainWeb.toWebCurrentGame(service.getGame(gameId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/game/{gameId}")
    public ResponseEntity<WebCurrentGame> sendGame(@PathVariable UUID gameId,
                                                   @RequestBody WebGameField field) {
        // TODO Разобраться где обрабатывать отправку поля некорректного размера
        // Если отправлена некорректна игра
        WebCurrentGame response =
                MapperDomainWeb.toWebCurrentGame(service.processUserMove(gameId, field.getField()));
        return ResponseEntity.ok(response);
    }
}
