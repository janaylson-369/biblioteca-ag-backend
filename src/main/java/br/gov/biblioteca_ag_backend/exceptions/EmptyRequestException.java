package br.gov.biblioteca_ag_backend.exceptions;

import lombok.Getter;

@Getter
public class EmptyRequestException extends RuntimeException {

    private final String campo;

    public EmptyRequestException(String campo, String mensagem) {
        super(mensagem);
        this.campo = campo;
    }
}
