package br.gov.biblioteca_ag_backend.dto.categoria;

import br.gov.biblioteca_ag_backend.model.Categoria;

public record CategoriaResumoDTO(
        Long id,
        String nome
) {
    public static CategoriaResumoDTO fromEntity(Categoria c) {
        return new CategoriaResumoDTO(
                c.getId(),
                c.getNome()
        );
    }
}
