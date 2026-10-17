package br.gov.biblioteca_ag_backend.dto.exemplar;

import jakarta.validation.constraints.NotNull;

public record CriarExemplarDTO(

        @NotNull(message = "campo nao pode ser nulo")
        Long id_livro,

        String localizaoEstante,

        String nTombamento
) {
}
