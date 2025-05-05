package com.smart_tiger.monio.middleware.exception;

public class JwtTokenBlackListFailureException extends RuntimeException {
    public JwtTokenBlackListFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
