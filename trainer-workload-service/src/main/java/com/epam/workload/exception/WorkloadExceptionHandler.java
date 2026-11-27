package com.epam.workload.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import jakarta.validation.ConstraintViolationException;
import java.util.Map;

@RestControllerAdvice
public class WorkloadExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WorkloadExceptionHandler.class);

    @ExceptionHandler(TrainerWorkloadNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(TrainerWorkloadNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Trainer workload not found"));
    }

    @ExceptionHandler(InvalidWorkloadChangeException.class)
    public ResponseEntity<Map<String, String>> handleInvalidChange(InvalidWorkloadChangeException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "Invalid workload change"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid workload request"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException exception) {
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid request parameters"));
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            WorkloadUsernameMismatchException.class
    })
    public ResponseEntity<Map<String, String>> handleMalformedRequest(Exception exception) {
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid request"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception exception) {
        LOG.error("Unexpected error while processing trainer workload", exception);
        return ResponseEntity.internalServerError()
                .body(Map.of("message", "Internal server error"));
    }
}
