package com.example.domain.service;

import com.example.domain.model.Move;

import java.util.UUID;

public interface GameService {
    Move nextTurn(UUID gameId);
    boolean validateField();
    boolean isGameOver(UUID gameId);
}
