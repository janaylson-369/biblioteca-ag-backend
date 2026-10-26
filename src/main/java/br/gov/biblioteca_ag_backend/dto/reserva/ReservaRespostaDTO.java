package br.gov.biblioteca_ag_backend.dto.reserva;

import br.gov.biblioteca_ag_backend.model.Reserva;

import java.time.LocalDateTime;

public record ReservaRespostaDTO(
        Long id,

        Long id_usuario,
        String nome_usuario,

        Long id_livro,
        String titulo,

        LocalDateTime dataReserva
) {
    public static ReservaRespostaDTO fromEntity(Reserva reserva) {
        return new ReservaRespostaDTO(
                reserva.getId(),
                reserva.getUsuario().getId(),
                reserva.getUsuario().getNome(),
                reserva.getLivro().getId(),
                reserva.getLivro().getTitulo(),
                reserva.getDataReserva());
    }
}
