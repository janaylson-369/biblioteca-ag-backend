package br.gov.biblioteca_ag_backend.dto.reserva;

import br.gov.biblioteca_ag_backend.enums.StatusReserva;
import br.gov.biblioteca_ag_backend.model.Livro;
import br.gov.biblioteca_ag_backend.model.Reserva;
import br.gov.biblioteca_ag_backend.model.Usuario;

import java.time.LocalDateTime;

public record ReservaRespostaDTO(
        Long id,

        Long id_usuario,
        String nome_usuario,

        Long id_livro,
        String titulo,

        LocalDateTime dataReserva,
        StatusReserva statusReserva
) {
    public static ReservaRespostaDTO fromEntity(Reserva reserva) {
        Usuario u = reserva.getUsuario();
        Livro l = reserva.getLivro();

        return new ReservaRespostaDTO(
                reserva.getId(),
                u.getId(),
                u.getNome(),
                l.getId(),
                l.getTitulo(),
                reserva.getDataReserva(),
                reserva.getStatus());
    }
}
