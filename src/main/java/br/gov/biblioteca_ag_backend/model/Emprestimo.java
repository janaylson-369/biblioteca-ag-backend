package br.gov.biblioteca_ag_backend.model;

import br.gov.biblioteca_ag_backend.enums.StatusEmprestimo;
import br.gov.biblioteca_ag_backend.enums.StatusExemplar;
import br.gov.biblioteca_ag_backend.exceptions.RequestValidationException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceAlreadyExistsException;
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
            throw new ResourceAlreadyExistsException(
                    "emprestimo já foi concluido",
                    String.format("emprestimo de id %d", id)
            );
        }

        setDataDevolucaoReal(LocalDateTime.now());
        setStatus(StatusEmprestimo.CONCLUIDO);
        exemplar.atualizarStatus(StatusExemplar.DISPONIVEL);
    }

    public void renovar(long diasAdicionais) {
        if (status == StatusEmprestimo.CONCLUIDO) {
            throw new RequestValidationException(
                    "Emprestimo já finalizado",
                    "Não é possível renovar um empréstimo concluído."
            );
        }
        if (dataPrevisaoDevolucao == null) {
            throw new RequestValidationException(
                    "Emprestimo sem data de devolução",
                    "O empréstimo não possui previsão de devolução."
            );
        }
        if (diasAdicionais <= 0) {
            throw new RequestValidationException(
                    "dias adicionais menor que 1",
                    "Os dias adicionais devem ser positivos."
            );
        }

        if (status != StatusEmprestimo.EM_ANDAMENTO) {
            throw new ResourceAlreadyExistsException(
                    "Emprestimo deve estar em andamento",
                    "Somente empréstimos em andamento podem ser renovados."
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
