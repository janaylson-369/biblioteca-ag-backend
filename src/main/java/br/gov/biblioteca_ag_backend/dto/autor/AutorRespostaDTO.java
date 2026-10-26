package br.gov.biblioteca_ag_backend.dto.autor;

import br.gov.biblioteca_ag_backend.model.Autor;

public record AutorRespostaDTO(
        Long id,
        String nome
) {
    public static AutorRespostaDTO fromEntity(Autor a) {
        return new AutorRespostaDTO(
                a.getId(),
                a.getNome()
        );
    }
}
