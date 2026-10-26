package br.gov.biblioteca_ag_backend.dto.reserva;

import jakarta.validation.constraints.NotNull;

public record CriarReservaDTO(

        @NotNull(message = "campo nao pode ser nulo")
        Long id_livro,

        @NotNull(message = "campo nao pode ser nulo")
        Long id_usuario
) {
}
