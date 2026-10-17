package br.gov.biblioteca_ag_backend.dto.dadosLeitor;

import br.gov.biblioteca_ag_backend.model.DadosLeitor;

import java.time.LocalDate;

public record DadosLeitorRespostaDTO(
        Long id,
        Long id_usuario,
        LocalDate dataNascimento,
        String logradouro,
        String numero,
        String complemento,
        String bairro
) {
    public static DadosLeitorRespostaDTO fromEntity(DadosLeitor dl) {
        return new DadosLeitorRespostaDTO(
          dl.getId(),
          dl.getUsuario().getId(),
          dl.getDataNascimento(),
          dl.getLogradouro(),
          dl.getNumero(),
          dl.getComplemento(),
          dl.getBairro()
        );
    }
}
