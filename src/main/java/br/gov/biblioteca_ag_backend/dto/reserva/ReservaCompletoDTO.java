package br.gov.biblioteca_ag_backend.dto.reserva;

import br.gov.biblioteca_ag_backend.dto.emprestimo.EmprestimoCurtoDTO;
import br.gov.biblioteca_ag_backend.enums.StatusReserva;
import br.gov.biblioteca_ag_backend.model.Reserva;

import java.time.LocalDateTime;

public record ReservaCompletoDTO(
        Long id,

        Long id_usuario,
        String nome_usuario,

        Long id_livro,
        String titulo,

        LocalDateTime dataReserva,
        StatusReserva statusReserva,

        Long id_emprestimo,
        EmprestimoCurtoDTO emprestimo
) {
    public static ReservaCompletoDTO fromEntity(Reserva r) {
        boolean emprestimoExiste = r.getEmprestimo() != null;

        return new ReservaCompletoDTO(
                r.getId(),
                r.getUsuario().getId(),
                r.getUsuario().getNome(),
                r.getLivro().getId(),
                r.getLivro().getTitulo(),
                r.getDataReserva(),
                r.getStatus(),
                emprestimoExiste ? r.getEmprestimo().getId() : null,
                emprestimoExiste ? EmprestimoCurtoDTO.fromEntity(r.getEmprestimo()) : null
        );
    }
}
