package com.example.web.dto;

import com.example.domain.model.GameStatus;
import com.example.domain.model.GameType;

import java.util.UUID;

public record WebCurrentGame(
        UUID id,
        WebGameField field,
        GameType type,
        GameStatus status,
        UUID playerXId,
        UUID playerOId
) {}