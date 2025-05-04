package com.smart_tiger.monio.middleware.exception;

public class BadRequestException extends Exception {

    public BadRequestException() {
        super("Invalid request was detected");
    }

}
