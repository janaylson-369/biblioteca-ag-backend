package br.gov.biblioteca_ag_backend.dto.dadosLeitor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDateTime;

public record CriarDadosLeitorDTO(
        @NotNull(message = "campo nao pode ser nulo")
        Long id_usuario,

        @NotNull(message = "campo nao pode ser nulo")
        @Past(message = "campo deve ser preenchido com data no passada")
        LocalDateTime dataNascimento,

        String logradouro,
        String numero,
        String complemento,
        String bairro
) {
}
