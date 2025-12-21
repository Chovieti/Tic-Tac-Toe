package com.example.domain.service;

import com.example.domain.model.Move;

public interface GameService {
    Move nextTurn();
    boolean validateField();
    boolean isGameOver();
}
