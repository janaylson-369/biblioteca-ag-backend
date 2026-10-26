package br.gov.biblioteca_ag_backend.dto.usuario;

import br.gov.biblioteca_ag_backend.model.Usuario;

public record UsuarioRespostaDTO(
        Long id,
        String nome,
        String telefone
) {
    public static UsuarioRespostaDTO fromEntity(Usuario usuario) {
        return new UsuarioRespostaDTO(usuario.getId(), usuario.getNome(), usuario.getTelefone());
    }
}
