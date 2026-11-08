package br.gov.biblioteca_ag_backend.dto.emprestimo;

import br.gov.biblioteca_ag_backend.model.Emprestimo;

import java.time.LocalDateTime;

public record EmprestimoCurtoDTO(
        Long id_emprestimo,
        Long id_exemplar,
        LocalDateTime dataPrevisaoDevolucao
) {
    public static EmprestimoCurtoDTO fromEntity(Emprestimo e) {
        return new EmprestimoCurtoDTO(
                e.getId(),
                e.getExemplar().getId(),
                e.getDataPrevisaoDevolucao()
        );
    }
}
