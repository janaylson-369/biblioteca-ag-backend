package br.gov.biblioteca_ag_backend.dto.livro;

import java.util.Set;

public record LivroRespostaDTO(
        Long id,
        Long id_categoria,
        Set<Long> id_autores,
        String titulo,
        String isbn,
        String sinopse,
        Integer anoPublicacao
) {
}