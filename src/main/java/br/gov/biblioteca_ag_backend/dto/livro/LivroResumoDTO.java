package br.gov.biblioteca_ag_backend.dto.livro;

public record LivroResumoDTO(
        Long id,
        String titulo,
        String isbn,
        String editora,
        String sinopse,
        Integer anoPublicacao
) {
}
