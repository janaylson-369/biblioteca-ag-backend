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

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    @NotNull
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_exemplar", nullable = false)
    @NotNull
    private Exemplar exemplar;

    @OneToOne
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;

    private LocalDateTime dataEmprestimo = LocalDateTime.now();

    private LocalDateTime dataprevisaoDevolucao;

    private LocalDateTime dataDevolucaoReal;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusEmprestimo status = StatusEmprestimo.EM_ANDAMENTO;

    public Emprestimo(Usuario usuario, Exemplar exemplar, LocalDateTime dataprevisaoDevolucao) {
        this.usuario = usuario;
        this.exemplar = exemplar;
        this.dataprevisaoDevolucao = dataprevisaoDevolucao;
    }

    public void registrarDevolucao() {
        setDataDevolucaoReal(LocalDateTime.now());
    }

    public void renovar(long diasAdicionais) {
        LocalDateTime dataDevolucaoMaisAdicionais = getDataprevisaoDevolucao().plusDays(diasAdicionais);
        setDataprevisaoDevolucao(dataDevolucaoMaisAdicionais);
    }

    public boolean verificarAtraso() {
        return (status == StatusEmprestimo.ATRASADO);
    }
}
