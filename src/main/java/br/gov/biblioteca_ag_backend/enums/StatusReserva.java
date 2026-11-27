package br.gov.biblioteca_ag_backend.enums;

import lombok.Getter;

@Getter
public enum StatusReserva {
    ATIVA(1),
    ATENDIDA(2),
    CANCELADA(3),
    EXPIRADA(4);

    private final int numeroReserva;

    StatusReserva(int numeroReserva) {
        this.numeroReserva = numeroReserva;
    }

}
