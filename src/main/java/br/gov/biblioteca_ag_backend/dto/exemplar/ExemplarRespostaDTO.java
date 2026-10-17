package br.gov.biblioteca_ag_backend.dto.exemplar;

import br.gov.biblioteca_ag_backend.model.Exemplar;

public record ExemplarRespostaDTO(
        Long id,

        Long id_livro,
        String titulo,

        String localizaoEstante,

        String nTombamento
) {
    public static ExemplarRespostaDTO fromEntity(Exemplar exemplar) {
        return new ExemplarRespostaDTO(
                exemplar.getId(),
                exemplar.getLivro().getId(),
                exemplar.getLivro().getTitulo(),
                exemplar.getLocalizacaoEstante(),
                exemplar.getNTombamento()
        );
    }
}
