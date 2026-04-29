package com.example.English_Learning_Platform.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final String details;

    public ValidationException(String message) {
        super(message);
        this.details = "";
    }
    public ValidationException(String message, String details) {
        super(message);
        this.details = details;
    }
}
