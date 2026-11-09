package br.gov.biblioteca_ag_backend.dto.dadosLeitor;

import br.gov.biblioteca_ag_backend.model.DadosLeitor;
import br.gov.biblioteca_ag_backend.model.Usuario;

public record DadosLeitorCompletoDTO (
        Long id_usuario,
        String nome,
        String telefone,
        String email,
        DadosLeitorResumoDTO dadosLeitor
) {
    public static DadosLeitorCompletoDTO fromEntity(Usuario u, DadosLeitor dl) {
        return new DadosLeitorCompletoDTO(
                u.getId(),
                u.getNome(),
                u.getTelefone(),
                u.getEmail(),
                DadosLeitorResumoDTO.fromEntity(dl)
        );
    }
}
