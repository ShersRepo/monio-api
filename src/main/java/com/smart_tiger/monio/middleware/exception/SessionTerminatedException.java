package com.smart_tiger.monio.middleware.exception;

public class SessionTerminatedException extends RuntimeException {
    public SessionTerminatedException(String message) {
        super(message);
    }
}
