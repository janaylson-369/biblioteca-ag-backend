package br.gov.biblioteca_ag_backend.dto.reserva;

import java.time.LocalDateTime;

public record ReservaRespostaDTO(
        Long id,

        Long id_usuario,
        String nome_usuario,

        Long id_livro,
        String titulo,

        LocalDateTime dataReserva
) {
}
