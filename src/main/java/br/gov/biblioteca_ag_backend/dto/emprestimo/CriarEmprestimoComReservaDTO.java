package br.gov.biblioteca_ag_backend.dto.emprestimo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CriarEmprestimoComReservaDTO(

        @NotNull(message = "campo nao deve ser nulo")
        Long id_reserva,
        @NotNull(message = "campo nao deve ser nulo")
        Long id_exemplar,

        @NotNull(message = "campo nao deve ser nulo")
        @Min(value = 3, message = "minimo de 3 dias para devolucao")
        @Max(value = 30, message = "maximo de 30 dias para devolucao")
        Long diasEmprestimo
) {
}
