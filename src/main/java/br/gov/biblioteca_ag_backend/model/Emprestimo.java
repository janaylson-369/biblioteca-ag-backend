package br.gov.biblioteca_ag_backend.model;

import br.gov.biblioteca_ag_backend.enums.StatusEmprestimo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_emprestimos")
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    @NotNull
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_exemplar", nullable = false)
    @NotNull
    private Exemplar exemplar;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;

    private LocalDateTime dataEmprestimo = LocalDateTime.now();

    private LocalDateTime dataPrevisaoDevolucao;

    private LocalDateTime dataDevolucaoReal;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusEmprestimo status = StatusEmprestimo.EM_ANDAMENTO;

    public Emprestimo(Usuario usuario, Exemplar exemplar, LocalDateTime dataPrevisaoDevolucao) {
        this.usuario = usuario;
        this.exemplar = exemplar;
        this.dataPrevisaoDevolucao = dataPrevisaoDevolucao;
    }

    public void registrarDevolucao() {
        if (status == StatusEmprestimo.CONCLUIDO) {
            throw new IllegalStateException(
                    "Este empréstimo já foi concluído."
            );
        }
        setDataDevolucaoReal(LocalDateTime.now());
        setStatus(StatusEmprestimo.CONCLUIDO);
    }

    public void renovar(long diasAdicionais) {
        if (status == StatusEmprestimo.CONCLUIDO) {
            throw new IllegalStateException(
                    "Não é possível renovar um empréstimo concluído."
            );
        }
        if (dataPrevisaoDevolucao == null) {
            throw new IllegalStateException(
                    "O empréstimo não possui previsão de devolução."
            );
        }
        if (diasAdicionais <= 0) {
            throw new IllegalArgumentException(
                    "Os dias adicionais devem ser positivos."
            );
        }
        LocalDateTime dataDevolucaoMaisAdicionais = getDataPrevisaoDevolucao().plusDays(diasAdicionais);
        setDataPrevisaoDevolucao(dataDevolucaoMaisAdicionais);
    }

    public boolean verificarAtraso() {
        return status != StatusEmprestimo.CONCLUIDO
                && dataPrevisaoDevolucao != null
                && LocalDateTime.now().isAfter(dataPrevisaoDevolucao);
    }
}
