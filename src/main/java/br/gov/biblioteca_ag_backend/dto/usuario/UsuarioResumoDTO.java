package br.gov.biblioteca_ag_backend.dto.usuario;

import br.gov.biblioteca_ag_backend.model.Usuario;

public record UsuarioResumoDTO(
        Long id,
        String nome,
        String telefone,
        String email
) {
    public static UsuarioResumoDTO fromEntity(Usuario u) {
        return new UsuarioResumoDTO(
                u.getId(),
                u.getNome(),
                u.getTelefone(),
                u.getEmail()
        );
    }
}
