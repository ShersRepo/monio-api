package com.smart_tiger.monio.middleware.exception;

public class DataTransactionFailed extends RuntimeException {

    private static final String DEFAULT_EXCEPTION_MESSAGE = "Transaction failed";

    public DataTransactionFailed() {
        super(DEFAULT_EXCEPTION_MESSAGE);
    }

    public DataTransactionFailed(String message) {
        super(message);
    }

}
