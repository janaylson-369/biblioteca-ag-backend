package br.gov.biblioteca_ag_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_autores")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "tb_livro_autor",
            joinColumns = @JoinColumn(name = "id_autor"),
            inverseJoinColumns = @JoinColumn(name = "id_livro"),
            uniqueConstraints = @UniqueConstraint(
                    columnNames = {"id_autor", "id_livro"}
            )
    )
    private Set<Livro> livros = new HashSet<>();

    @Column(nullable = false)
    @NotBlank(message = "campo não pode ser vazio.")
    private String nome;

    public Autor(Set<Livro> livros, String nome) {
        this.livros = livros;
        this.nome = nome;
    }
}
