package br.gov.biblioteca_ag_backend.exceptions;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class RequestValidationException extends RuntimeException {

    private final Map<String, String> errors;

    public RequestValidationException(Map<String, String> errors) {
        super("Erro de validação da requisição");
        this.errors = Map.copyOf(errors);
    }

    public RequestValidationException(String campo, String erro) {
        super("Erro de validação da requisição");
        Map<String, String> errors = new HashMap<>();
        errors.put(campo, erro);
        this.errors = errors;
    }
}
