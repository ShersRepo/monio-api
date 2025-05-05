package com.smart_tiger.monio.middleware.exception;

public class JwtStorageFailureException extends RuntimeException {
    public JwtStorageFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
