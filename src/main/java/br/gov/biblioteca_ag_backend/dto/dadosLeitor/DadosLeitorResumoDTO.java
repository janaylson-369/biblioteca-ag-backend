package br.gov.biblioteca_ag_backend.dto.dadosLeitor;

import java.time.LocalDateTime;

public record DadosLeitorResumoDTO(
        Long id,
        LocalDateTime dataNascimento,
        String logradouro,
        String numero,
        String complemento,
        String bairro
) {
}

