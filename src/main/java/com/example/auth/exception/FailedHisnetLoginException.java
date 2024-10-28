package com.example.auth.exception;

import lombok.Getter;

@Getter
public class FailedHisnetLoginException extends RuntimeException {
    private final Integer status;

    public FailedHisnetLoginException(String message, Integer status) {
        super(message);
        this.status = status;
    }
}
