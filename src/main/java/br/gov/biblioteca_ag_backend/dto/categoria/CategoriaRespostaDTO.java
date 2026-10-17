package br.gov.biblioteca_ag_backend.dto.categoria;

import br.gov.biblioteca_ag_backend.dto.livro.LivroResumoDTO;


import java.util.List;

public record CategoriaRespostaDTO(
        Long id,
        String nome,
        List<LivroResumoDTO> livros
) {
}
