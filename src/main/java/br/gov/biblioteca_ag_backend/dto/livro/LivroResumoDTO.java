package br.gov.biblioteca_ag_backend.dto.livro;

import br.gov.biblioteca_ag_backend.dto.autor.AutorRespostaDTO;
import br.gov.biblioteca_ag_backend.model.Livro;

import java.util.List;

public record LivroResumoDTO(
        Long id,
        String categoria,
        String titulo,
        String isbn,
        String editora,
        String sinopse,
        Integer anoPublicacao,
        List<AutorRespostaDTO> autores
) {
    public static LivroResumoDTO fromEntity(Livro livro) {
        return new LivroResumoDTO(
                livro.getId(),
                (livro.getCategoria() != null ? livro.getCategoria().getNome() : ""),
                livro.getTitulo(),
                livro.getIsbn(),
                livro.getEditora(),
                livro.getSinopse(),
                livro.getAnoPublicacao(),
                livro.getAutores().stream().map(AutorRespostaDTO::fromEntity).toList()
        );
    }

}
