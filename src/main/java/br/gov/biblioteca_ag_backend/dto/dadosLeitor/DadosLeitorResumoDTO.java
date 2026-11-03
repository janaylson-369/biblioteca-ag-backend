package br.gov.biblioteca_ag_backend.dto.dadosLeitor;

import br.gov.biblioteca_ag_backend.model.DadosLeitor;

import java.time.LocalDate;

public record DadosLeitorResumoDTO(
        LocalDate dataNascimento,
        String logradouro,
        String numero,
        String complemento,
        String bairro
) {
    public static DadosLeitorResumoDTO fromEntity(DadosLeitor d) {
        return new DadosLeitorResumoDTO(
                d.getDataNascimento(),
                d.getLogradouro(),
                d.getNumero(),
                d.getComplemento(),
                d.getBairro()
        );
    }
}

