package br.gov.biblioteca_ag_backend.dto.emprestimo;

import br.gov.biblioteca_ag_backend.dto.exemplar.ExemplarRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.usuario.UsuarioRespostaDTO;
import br.gov.biblioteca_ag_backend.enums.StatusEmprestimo;
import br.gov.biblioteca_ag_backend.model.Emprestimo;

import java.time.LocalDateTime;

public record EmprestimoCompletoDTO(
        Long id,

        UsuarioRespostaDTO usuario,

        ExemplarRespostaDTO exemplar,

        Long id_reserva,

        LocalDateTime dataEmprestimo,
        LocalDateTime dataPrevisaoDevolucao,
        LocalDateTime dataDevolucaoReal,
        StatusEmprestimo statusEmprestimo
) {
    public static EmprestimoCompletoDTO fromEntity(Emprestimo e) {
        boolean reservaExiste = e.getReserva() != null;
        boolean isDevolvido = e.getDataDevolucaoReal() != null;

        return new EmprestimoCompletoDTO(
                e.getId(),
                UsuarioRespostaDTO.fromEntity(e.getUsuario()),
                ExemplarRespostaDTO.fromEntity(e.getExemplar()),
                (reservaExiste) ? e.getReserva().getId() : null,
                e.getDataEmprestimo(),
                e.getDataPrevisaoDevolucao(),
                (isDevolvido) ? e.getDataDevolucaoReal() : null,
                e.getStatus()
        );
    }
}
