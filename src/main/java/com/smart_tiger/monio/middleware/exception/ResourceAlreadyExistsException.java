package com.smart_tiger.monio.middleware.exception;

public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(String resourceName, String id) {
        super(resourceName + " with id '" + id + "' already exists");
    }

}
