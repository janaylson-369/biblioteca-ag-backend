package br.gov.biblioteca_ag_backend.exceptions;

import lombok.Getter;

import java.util.Map;

@Getter
public class RequestValidationException extends RuntimeException {

    private final Map<String, String> errors;

    public RequestValidationException(Map<String, String> errors) {
        super("Erro de validação da requisição");
        this.errors = Map.copyOf(errors);
    }
}
