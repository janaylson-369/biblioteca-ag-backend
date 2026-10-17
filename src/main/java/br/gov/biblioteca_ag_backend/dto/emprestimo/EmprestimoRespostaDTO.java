package br.gov.biblioteca_ag_backend.dto.emprestimo;

import br.gov.biblioteca_ag_backend.model.Emprestimo;

import java.time.LocalDateTime;

public record EmprestimoRespostaDTO(
        Long id,

        Long id_usuario,
        String nome_usuario,

        Long id_exemplar,
        String titulo,

        LocalDateTime dataPrevisaoDevolucao
) {
    public static EmprestimoRespostaDTO fromEntity(Emprestimo em) {
        return new EmprestimoRespostaDTO(
                em.getId(),
                em.getUsuario().getId(),
                em.getUsuario().getNome(),
                em.getExemplar().getId(),
                em.getExemplar().getLivro().getTitulo(),
                em.getDataPrevisaoDevolucao()
        );
    }
}
