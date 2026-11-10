package br.gov.biblioteca_ag_backend.dto.exemplar;

import jakarta.validation.constraints.Size;

public record AtualizarExemplarDTO(

        @Size(max = 255, message = "campo deve ter menos de 255 caracteres")
        String localizaoEstante,


        @Size(max = 255, message = "campo deve ter menos de 255 caracteres")
        String nTombamento
) {
}
