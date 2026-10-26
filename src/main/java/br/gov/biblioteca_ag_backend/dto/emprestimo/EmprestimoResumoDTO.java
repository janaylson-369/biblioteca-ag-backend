package br.gov.biblioteca_ag_backend.dto.emprestimo;

import java.time.LocalDateTime;

public record EmprestimoResumoDTO(
        Long id,

        Long id_usuario,
        String nome_usuario,

        Long id_exemplar,
        String titulo,

        LocalDateTime dataPrevisaoDevolucao
) {
}
