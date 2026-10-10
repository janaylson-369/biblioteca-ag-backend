package br.gov.biblioteca_ag_backend.model;

import br.gov.biblioteca_ag_backend.enums.StatusExemplar;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_exemplares")
public class Exemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_livro", nullable = false)
    @NotNull
    private Livro livro;

    @OneToMany(mappedBy = "exemplar")
    @NotNull
    private List<Emprestimo> emprestimos;

    private String localizacaoEstante;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "campo nao pode ser vazio")
    private StatusExemplar status;

    @Column(name = "n_tombamento", unique = true)
    private String nTombamento;
}
