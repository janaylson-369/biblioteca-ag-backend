package br.gov.biblioteca_ag_backend.dto.exemplar;

import br.gov.biblioteca_ag_backend.enums.StatusExemplar;
import br.gov.biblioteca_ag_backend.model.Exemplar;

public record ExemplarResumoDTO(
        Long id,

        Long id_livro,
        String titulo,

        String localizaoEstante,

        String nTombamento,
        StatusExemplar statusExemplar
) {
    public static ExemplarResumoDTO fromEntity (Exemplar ex) {
        return new ExemplarResumoDTO(
                ex.getId(),
                ex.getLivro().getId(),
                ex.getLivro().getTitulo(),
                ex.getLocalizacaoEstante(),
                ex.getNTombamento(),
                ex.getStatus()
        );
    }
}
