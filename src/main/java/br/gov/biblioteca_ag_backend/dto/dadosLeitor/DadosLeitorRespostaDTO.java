package br.gov.biblioteca_ag_backend.dto.dadosLeitor;

import java.time.LocalDateTime;

public record DadosLeitorRespostaDTO(
        Long id,
        Long id_usuario,
        LocalDateTime dataNascimento,
        String logradouro,
        String numero,
        String complemento,
        String bairro
) {
}
