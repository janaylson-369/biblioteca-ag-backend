package br.gov.biblioteca_ag_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "categoria")
    private List<Livro> livros;

    @NotBlank(message = "campo não pode ser vazio.")
    private String nome;

    public Categoria(List<Livro> livros, String nome) {
        this.livros = livros;
        this.nome = nome;
    }
}
