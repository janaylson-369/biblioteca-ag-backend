package br.gov.biblioteca_ag_backend.dto.emprestimo;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CriarEmprestimoDTO(
        Long id_reserva,

        @NotNull(message = "campo nao deve ser nulo")
        Long id_usuario,

        @NotNull(message = "campo nao deve ser nulo")
        Long id_exemplar,

        @NotNull(message = "campo nao deve ser nulo")
        @Future(message = "invalido, campo deve possuir data no futuro")
        LocalDateTime dataPrevisaoDevolucao
) {
}
