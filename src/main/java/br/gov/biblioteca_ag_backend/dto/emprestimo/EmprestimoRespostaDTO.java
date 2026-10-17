package br.gov.biblioteca_ag_backend.dto.emprestimo;

import java.time.LocalDateTime;

public record EmprestimoRespostaDTO(
        Long id,

        Long id_usuario,
        String nome_usuario,

        Long id_exemplar,
        Long titulo,

        LocalDateTime dataPrevisaoDevolucao
) {
}
