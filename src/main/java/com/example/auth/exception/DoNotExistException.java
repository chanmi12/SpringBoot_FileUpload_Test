package com.example.auth.exception;

public class DoNotExistException extends RuntimeException {
    public DoNotExistException(String message) {
        super(message);
    }
}
