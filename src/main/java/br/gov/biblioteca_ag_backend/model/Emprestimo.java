package br.gov.biblioteca_ag_backend.model;

import br.gov.biblioteca_ag_backend.enums.StatusEmprestimo;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_emprestimos")
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataEmprestimo;

    private LocalDateTime dataprevisaoDevolucao;

    private LocalDateTime dataDevolucaoReal;

    private StatusEmprestimo status;
}
