package com.smart_tiger.monio.middleware.response;

import org.springframework.http.HttpStatus;

import java.util.Collections;

public class SuccessResponseType {

    private SuccessResponseType() {
    }

    public static ApiResponse<Void> ok() {
        return ApiResponse.<Void> builder()
                .errors(Collections.emptyList())
                .message("Success")
                .status(HttpStatus.OK.value())
                .data(null)
                .build();
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return ApiResponse.<T> builder()
                .errors(Collections.emptyList())
                .message(message)
                .status(HttpStatus.OK.value())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> okFetch(T data) {
        return ApiResponse.<T> builder()
                .errors(Collections.emptyList())
                .message("Fetched data")
                .status(HttpStatus.OK.value())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> okPatch(T data) {
        return ApiResponse.<T> builder()
                .errors(Collections.emptyList())
                .message("Patched data")
                .status(HttpStatus.OK.value())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T> builder()
                .errors(Collections.emptyList())
                .message("Created")
                .status(HttpStatus.CREATED.value())
                .data(data)
                .build();
    }

    public static ApiResponse<Void> processing(String message) {
        return ApiResponse.<Void> builder()
                .errors(Collections.emptyList())
                .message(message)
                .status(HttpStatus.ACCEPTED.value())
                .data(null)
                .build();
    }

    public static ApiResponse<Void> okWithoutDataResponse(String message) {
        return ApiResponse.<Void> builder()
                .errors(Collections.emptyList())
                .message(message)
                .status(HttpStatus.NO_CONTENT.value())
                .data(null)
                .build();
    }

}
