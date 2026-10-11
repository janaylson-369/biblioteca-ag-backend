package br.gov.biblioteca_ag_backend.model;

import br.gov.biblioteca_ag_backend.enums.StatusExemplar;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
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
    private List<Emprestimo> emprestimos = new ArrayList<>();

    private String localizacaoEstante;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusExemplar status = StatusExemplar.DISPONIVEL;

    @Column(name = "n_tombamento", unique = true)
    private String nTombamento;

    public Exemplar(Livro livro, String localizacaoEstante, String nTombamento) {
        this.livro = livro;
        this.localizacaoEstante = localizacaoEstante;
        this.nTombamento = nTombamento;
    }

    public void atualizarStatus(StatusExemplar novoStatus) {
        setStatus(novoStatus);
    }

    public boolean estaDisponivel() {
        return (status == StatusExemplar.DISPONIVEL);
    }
}
