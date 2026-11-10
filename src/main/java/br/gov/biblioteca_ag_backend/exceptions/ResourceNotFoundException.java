package br.gov.biblioteca_ag_backend.exceptions;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String campo;

    public ResourceNotFoundException(String campo, String mensagem) {
        super(mensagem);
        this.campo = campo;
    }
}