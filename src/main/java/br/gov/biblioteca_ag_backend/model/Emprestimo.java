package br.gov.biblioteca_ag_backend.model;

import br.gov.biblioteca_ag_backend.enums.StatusEmprestimo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

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

    private LocalDateTime dataEmprestimo;

    private LocalDateTime dataprevisaoDevolucao;

    private LocalDateTime dataDevolucaoReal;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusEmprestimo status;
}
