package com.epam.exception;

import com.epam.dto.response.ApiErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(UserNotFoundException e) {
        LOG.error("User not found", e);
        return build(HttpStatus.NOT_FOUND, "User not found");
    }

    @ExceptionHandler(TrainingTypeNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(TrainingTypeNotFoundException e) {
        LOG.error("Training type not found", e);
        return build(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(ValidationException e) {
        LOG.error("Validation failed", e);
        return build(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuth(AuthenticationException e) {
        LOG.error("Authentication failed", e);
        return build(HttpStatus.UNAUTHORIZED, "Authentication failed");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleDenied(AccessDeniedException e) {
        LOG.error("Access denied", e);
        return build(HttpStatus.FORBIDDEN, "Access denied");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleBeanValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        LOG.error("Request validation failed: {}", message);
        return build(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        LOG.error("Type mismatch for parameter: {}", e.getName());
        return build(HttpStatus.BAD_REQUEST, "Invalid value for parameter: " + e.getName());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception e) {
        LOG.error("Unhandled exception", e);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message) {
        ApiErrorResponse body = new ApiErrorResponse(message);
        return ResponseEntity.status(status).body(body);
    }
}
