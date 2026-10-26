package br.gov.biblioteca_ag_backend.dto.livro;

import java.util.Set;

public record AtualizarLivroDTO(
        String titulo,
        String isbn,
        String editora,
        String sinopse,
        Integer anoPublicacao,
        Long id_categoria,
        Set<Long> id_autores
) {
}