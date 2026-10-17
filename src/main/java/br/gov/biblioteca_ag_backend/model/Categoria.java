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
@Table(name = "tb_categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "categoria", fetch = FetchType.EAGER)
    private Set<Livro> livros = new HashSet<>();

    @NotBlank(message = "campo não pode ser vazio.")
    private String nome;

    public Categoria(String nome) {
        this.nome = nome;
    }

    public void adicionarLivro(Livro livro) {
        getLivros().add(livro);
    }

}
