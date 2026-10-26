package br.gov.biblioteca_ag_backend.dto.livro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CriarLivroDTO(
        Long id_categoria,

        Set<Long> id_autores,

        @NotBlank(message = "campo nao deve ficar em branco")
        @Size(max = 255, message = "campo deve ter menos de 255 caracteres")
        String titulo,

        @Size(max = 255)
        String isbn,

        @Size(max = 255)
        String editora,

        String sinopse,

        Integer anoPublicacao

) {
}
