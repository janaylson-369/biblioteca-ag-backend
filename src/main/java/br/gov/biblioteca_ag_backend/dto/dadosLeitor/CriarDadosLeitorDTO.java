package br.gov.biblioteca_ag_backend.dto.dadosLeitor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CriarDadosLeitorDTO(
        @NotNull(message = "campo nao pode ser nulo")
        @Past(message = "campo deve ser preenchido com data no passada")
        LocalDate dataNascimento,

        @Size(max = 255, message = "campo deve ter menos de 255 caracteres")
        String logradouro,

        @Size(max = 255, message = "campo deve ter menos de 255 caracteres")
        String numero,

        @Size(max = 255, message = "campo deve ter menos de 255 caracteres")
        String complemento,

        @Size(max = 255, message = "campo deve ter menos de 255 caracteres")
        String bairro
) {
}
