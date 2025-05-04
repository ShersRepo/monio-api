package com.smart_tiger.monio.middleware.validation;

import java.util.Optional;

public enum ValidationFailure {

    NOT_NULL("Field cannot be null"),
    NOT_BLANK("Field cannot be blank"),
    MIN_LENGTH("Field must meet the minimum length"),
    MAX_LENGTH("Field must not exceed the maximum length"),
    INVALID_LENGTH("Field is an invalid length"),
    EMAIL("Field must be a valid email address"),
    PASSWORD("Field must meet the password complexity requirements"),
    SOMETHING_WENT_WRONG("An unexpected error occurred");

    private String message;

    ValidationFailure(String message) {
        this.message = message;
    }

    public static Optional<ValidationFailure> fromMessage(String failureMessage) {
        for (ValidationFailure type : values()) {
            if (type.name().equalsIgnoreCase(failureMessage)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    public String getMessage() {
        return this.message;
    }

}
