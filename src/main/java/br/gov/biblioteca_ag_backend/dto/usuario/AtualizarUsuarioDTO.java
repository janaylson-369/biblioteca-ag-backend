package br.gov.biblioteca_ag_backend.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AtualizarUsuarioDTO(

        @Pattern(regexp = ".*\\S.*", message = "O campo não pode estar vazio")
        @Size(max = 255, message = "campo deve ter menos de 255 caracteres")
        String nome,

        @Size(max = 20, message = "campo deve ter menos de 20 caracteres.")
        String telefone,

        @Email(message = "campo deve ser preenchido com um email.")
        String email) {
}
