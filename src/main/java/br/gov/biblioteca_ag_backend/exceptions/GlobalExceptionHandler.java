
package br.gov.biblioteca_ag_backend.exceptions;

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
