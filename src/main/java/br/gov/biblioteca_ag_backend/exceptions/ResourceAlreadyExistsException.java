package br.gov.biblioteca_ag_backend.exceptions;

import lombok.Getter;

@Getter
public class ResourceAlreadyExistsException extends RuntimeException {

    private final String campo;

    public ResourceAlreadyExistsException(String campo, String mensagem) {
        super(mensagem);
        this.campo = campo;
    }
}
