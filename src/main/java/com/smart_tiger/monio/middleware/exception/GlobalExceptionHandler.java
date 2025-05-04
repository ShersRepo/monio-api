package com.smart_tiger.monio.middleware.exception;

import com.smart_tiger.monio.middleware.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.smart_tiger.monio.middleware.validation.ValidationUtil.extractFieldErrorMessage;


@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Status Code - 500
     *
     * @param ex Handle all internal server exceptions here
     * @return Response with exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> allUnhandledExceptions(Exception ex) {
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null, null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiResponse);
    }

    /**
     * Status Code - 404
     *
     * @param ex Resource not found
     * @return Response with exception
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> resourceNotFound(ResourceNotFoundException ex) {
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null, null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    /**
     * Status Code - 401
     *
     * @param ex Request was not authorised and therefore could not be processed
     * @return Response with exception
     */
    @ExceptionHandler(NotAuthorisedException.class)
    public ResponseEntity<ApiResponse<Void>> notAuthorised(NotAuthorisedException ex) {
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), null, null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    /**
     * Status Code - 409
     *
     * @param ex Exception for resources that had a conflict and therefore could not be processed
     * @return Response with exception
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> conflictingResources(ResourceAlreadyExistsException ex) {
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.CONFLICT.value(), ex.getMessage(), null, null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    /**
     * Status Code - 400
     *
     * @param ex Catches invalid object validations (in payloads for DTOs)
     * @return Response with exception
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> failedDTOValidation(MethodArgumentNotValidException ex) {
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid request found", null, null);
        List<Map<String, String>> fieldsInError = ex.getBindingResult().getFieldErrors().stream()
                .map(x -> {
                    Map<String, String> result = new HashMap<>();
                    result.put("field", x.getField());
                    result.put("message", extractFieldErrorMessage(x).getMessage());
                    return result;
                })
                .toList();
        apiResponse.setErrors(fieldsInError);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    /**
     * Status Code - 400
     *
     * @param ex Used when a request had bad parameters/arguments/endpoints
     * @return Response with exception
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> badRequestHandler(BadRequestException ex) {
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    /**
     * Status Code - 409
     *
     * @param ex Failed MOST likely due to dependent resources. TODO precisely implement in the services
     * @return Response with exception
     */
    @ExceptionHandler(ResourceCouldNotBeDeletedException.class)
    public ResponseEntity<ApiResponse<Void>> resourceCouldNotBeDeleted(ResourceCouldNotBeDeletedException ex) {
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.CONFLICT.value(), ex.getMessage(), null, null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
    }

}
