package com.example.domain.exception;

public class UserDoesntExistsException extends RuntimeException {
    public UserDoesntExistsException(String message) {
        super(message);
    }
}
