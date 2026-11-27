package br.gov.biblioteca_ag_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_auditoria_acesso")
public class AuditoriaAcesso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Long idAuditoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_operador", nullable = false, updatable = false)
    @NotNull(message = "O operador responsável pela ação é obrigatório.")
    private Usuario operador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_titular_afetado", nullable = false, updatable = false)
    @NotNull(message = "O titular afetado pela ação é obrigatório.")
    private Usuario titularAfetado;

    @Column(nullable = false, length = 100, updatable = false)
    @NotBlank(message = "A ação realizada é obrigatória.")
    @Size(max = 100, message = "A ação deve ter no máximo 100 caracteres.")
    private String acao;

    @Column(length = 500, updatable = false)
    @Size(max = 500, message = "A justificativa deve ter no máximo 500 caracteres.")
    private String justificativa;

    @Column(name = "data_hora", nullable = false, updatable = false)
    private LocalDateTime dataHora;

    public AuditoriaAcesso(Usuario operador, Usuario titularAfetado, String acao, String justificativa) {
        if (operador == null) {
            throw new IllegalArgumentException("O operador da auditoria não pode ser nulo.");
        }
        if (titularAfetado == null) {
            throw new IllegalArgumentException("O titular afetado não pode ser nulo.");
        }
        if (acao == null || acao.isBlank()) {
            throw new IllegalArgumentException("A ação auditada é obrigatória.");
        }

        this.operador = operador;
        this.titularAfetado = titularAfetado;
        this.acao = acao;
        this.justificativa = justificativa;
        this.dataHora = LocalDateTime.now();
    }

    public Long getIdOperador() {
        return this.operador != null ? this.operador.getIdUsuario() : null;
    }

    public Long getIdTitularAfetado() {
        return this.titularAfetado != null ? this.titularAfetado.getIdUsuario() : null;
    }
}