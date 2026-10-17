package br.gov.biblioteca_ag_backend.dto.usuario;

import br.gov.biblioteca_ag_backend.dto.dadosLeitor.DadosLeitorResumoDTO;

public record UsuarioResumoDTO(
        Long id,
        String nome,
        String telefone,
        DadosLeitorResumoDTO dadosLeitos
) {
}
