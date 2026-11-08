package br.gov.biblioteca_ag_backend.dto.autor;

import br.gov.biblioteca_ag_backend.dto.livro.LivroRespostaDTO;
import br.gov.biblioteca_ag_backend.model.Autor;

import java.util.Set;
import java.util.stream.Collectors;

public record AutorResumoDTO(
        Long id,
        String nome,
        Set<LivroRespostaDTO> livros
) {
    public static AutorResumoDTO fromEntity(Autor a) {
        return new AutorResumoDTO(
                a.getId(),
                a.getNome(),
                a.getLivros().stream()
                        .map(LivroRespostaDTO::fromEntity)
                        .collect(Collectors.toSet())
        );
    }
}
