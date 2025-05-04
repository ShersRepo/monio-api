package com.smart_tiger.monio.middleware.exception;

public class ResourceCouldNotBeDeletedException extends Exception  {

    public ResourceCouldNotBeDeletedException(String resourceName, String id) {
        super(resourceName + " with id '" + id + "' could not be deleted");
    }

}
