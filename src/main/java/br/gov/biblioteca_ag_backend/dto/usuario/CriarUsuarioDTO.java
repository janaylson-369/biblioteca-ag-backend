package br.gov.biblioteca_ag_backend.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarUsuarioDTO(
        @NotBlank(message = "campo nao deve ficar em branco")
        @Size(max = 255, message = "campo deve ter menos de 255 caracteres")
        String nome,

        @Size(min = 1, max = 20, message = "campo deve ficar entre 1 e 20 caracteres.")
        String telefone,

        @Email(message = "campo deve ser preenchido com um email.")
        String email,

        @NotBlank(message = "campo nao deve ficar em branco")
        @Size(min = 5, max = 255, message = "campo deve ficar entre 5 e 255 caracteres")
        String senha_normal
) {
}
