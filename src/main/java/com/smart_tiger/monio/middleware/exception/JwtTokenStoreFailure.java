package com.smart_tiger.monio.middleware.exception;

public class JwtTokenStoreFailure extends RuntimeException {

    public JwtTokenStoreFailure(String message, Throwable cause) {
        super(message, cause);
    }
}
