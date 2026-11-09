package br.gov.biblioteca_ag_backend.dto.exemplar;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarExemplarDTO(

        @NotNull(message = "campo nao pode ser nulo")
        Long id_livro,

        @Size(max = 255, message = "campo deve ter menos de 255 caracteres")
        String localizaoEstante,


        @Size(max = 255, message = "campo deve ter menos de 255 caracteres")
        String nTombamento
) {
}
