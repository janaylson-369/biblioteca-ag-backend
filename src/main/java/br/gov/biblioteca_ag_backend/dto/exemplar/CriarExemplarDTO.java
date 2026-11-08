package br.gov.biblioteca_ag_backend.dto.exemplar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarExemplarDTO(

        @NotNull(message = "campo nao pode ser nulo")
        Long id_livro,

        @Size(max = 255, message = "campo deve ter menos de 255 caracteres")
        String localizaoEstante,

        @NotBlank(message = "número de tombamento é obrigatório")
        @Size(max = 255, message = "campo deve ter menos de 255 caracteres")
        String nTombamento
) {
}
