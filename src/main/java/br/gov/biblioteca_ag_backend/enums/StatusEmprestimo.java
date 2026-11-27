package br.gov.biblioteca_ag_backend.enums;


import lombok.Getter;

@Getter
public enum StatusEmprestimo {
    EM_ANDAMENTO(1),
    CONCLUIDO(2),
    ATRASADO(3);

    private final int tipoEmprestimo;

    StatusEmprestimo(int tipoEmprestimo) {
        this.tipoEmprestimo = tipoEmprestimo;
    }

}
