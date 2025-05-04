package com.smart_tiger.monio.middleware.exception;

public class ResourceCouldNotCreated extends RuntimeException {

    public ResourceCouldNotCreated(String id) {
        super("Failed to create " + id);
    }

}
