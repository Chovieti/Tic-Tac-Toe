package com.example.domain.exception;

public class GameNotJoinableException extends RuntimeException {
    public GameNotJoinableException(String message) {
        super(message);
    }
}
