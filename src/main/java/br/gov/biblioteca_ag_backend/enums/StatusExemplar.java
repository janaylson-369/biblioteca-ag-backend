package br.gov.biblioteca_ag_backend.enums;

import lombok.Getter;

@Getter
public enum StatusExemplar {
    DISPONIVEL(1),
    EMPRESTADO(2),
    MANUTENCAO(3),
    EXTRAVIADO(4);

    private final int numeroStartus;

    StatusExemplar(int numeroStartus) {
        this.numeroStartus = numeroStartus;
    }

}
