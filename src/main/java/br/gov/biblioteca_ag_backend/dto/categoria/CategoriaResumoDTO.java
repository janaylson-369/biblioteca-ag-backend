package br.gov.biblioteca_ag_backend.dto.categoria;

import br.gov.biblioteca_ag_backend.dto.livro.LivroRespostaDTO;
import br.gov.biblioteca_ag_backend.model.Categoria;

import java.util.List;

public record CategoriaResumoDTO(
        Long id,
        String nome,
        List<LivroRespostaDTO> livros
) {
    public static CategoriaResumoDTO fromEntity(Categoria c) {
        return new CategoriaResumoDTO(
                c.getId(),
                c.getNome(),
                c.getLivros().stream().map(LivroRespostaDTO::fromEntity).toList()
        );
    }
}
