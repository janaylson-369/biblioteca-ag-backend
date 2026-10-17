package br.gov.biblioteca_ag_backend.dto.exemplar;

public record ExemplarResumoDTO(
        Long id,

        Long id_livro,
        String titulo,

        String localizaoEstante,

        String nTombamento
) {
}
