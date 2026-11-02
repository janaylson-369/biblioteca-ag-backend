package br.gov.biblioteca_ag_backend.dto.exemplar;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record AtualizarExemplarDTO(

        @Min(value = 1)
        Long id_livro,

        @Size(max = 255, message = "campo deve ter menos de 255 caracteres")
        String localizaoEstante,


        @Size(max = 255, message = "campo deve ter menos de 255 caracteres")
        String nTombamento
) {
}
