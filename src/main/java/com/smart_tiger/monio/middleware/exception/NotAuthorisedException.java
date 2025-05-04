package com.smart_tiger.monio.middleware.exception;

public class NotAuthorisedException extends Exception {

    public NotAuthorisedException() {
        super("Not authorised to perform this action.");
    }

}
