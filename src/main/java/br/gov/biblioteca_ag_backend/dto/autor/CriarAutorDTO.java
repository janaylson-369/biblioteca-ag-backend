package br.gov.biblioteca_ag_backend.dto.autor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarAutorDTO(
        @NotBlank(message = "campo não deve ficar em branco")
        @Size(max = 255, message = "campo deve possuir menos de 255 caracteres")
        String nome
) {
}
