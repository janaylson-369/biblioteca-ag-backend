
package br.gov.biblioteca_ag_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            RequestValidationException ex) {

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errors", ex.getErrors());

        return ResponseEntity
                .badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getCampo(), ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(EmptyRequestException.class)
    public ResponseEntity<Map<String, Object>> handleEmptyRequest(
            EmptyRequestException ex) {

        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getCampo(), ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmptyRequest(
            ResourceAlreadyExistsException ex) {

        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getCampo(), ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(
            org.springframework.web.bind.MethodArgumentNotValidException.class
    )
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);

        return ResponseEntity
                .badRequest()
                .body(errorResponse);
    }
}
