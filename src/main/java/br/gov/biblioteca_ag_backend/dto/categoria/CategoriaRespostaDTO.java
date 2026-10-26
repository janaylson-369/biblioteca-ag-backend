package br.gov.biblioteca_ag_backend.dto.categoria;

import br.gov.biblioteca_ag_backend.model.Categoria;

public record CategoriaRespostaDTO(
        Long id,
        String nome
) {
    public static CategoriaRespostaDTO fromEntity(Categoria c) {
        return new CategoriaRespostaDTO(
                c.getId(),
                c.getNome()
        );
    }
}
