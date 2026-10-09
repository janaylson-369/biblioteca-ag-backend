package br.gov.biblioteca_ag_backend.model;

import br.gov.biblioteca_ag_backend.enums.StatusExemplar;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_exemplar")
public class Exemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_livro", nullable = false)
    private Livro livro;

    private String localizacaoEstante;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "campo nao pode ser vazio")
    private StatusExemplar status;

    @Column(name = "n_tombamento", unique = true)
    private String nTombamento;
}
