package com.smart_tiger.monio.middleware.exception;

public class JwtTokenRotationException extends RuntimeException {
    public JwtTokenRotationException(String message, Throwable cause) {
        super(message, cause);
    }
}
