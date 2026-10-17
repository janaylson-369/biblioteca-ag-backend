package br.gov.biblioteca_ag_backend.dto.livro;

import br.gov.biblioteca_ag_backend.model.Livro;

public record LivroRespostaDTO(
        Long id,
        String categoria,
        String titulo,
        String isbn,
        String sinopse,
        Integer anoPublicacao
) {
    public static LivroRespostaDTO fromEntity(Livro livro) {
        return new LivroRespostaDTO(
                livro.getId(),
                livro.getCategoria().getNome(),
                livro.getTitulo(),
                livro.getIsbn(),
                livro.getSinopse(),
                livro.getAnoPublicacao()
        );
    }
}