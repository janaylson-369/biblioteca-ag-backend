package br.gov.biblioteca_ag_backend.dto.exemplar;

public record ExemplarRespostaDTO(
        Long id,

        Long id_livro,
        String titulo,

        String localizaoEstante,

        String nTombamento
) {
}
