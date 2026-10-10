package br.gov.biblioteca_ag_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_livros")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_categoria", unique = true)
    private Categoria categoria;

    @ManyToMany(mappedBy = "livros")
    private Set<Autor> autores;

    @OneToMany(mappedBy = "livro")
    private List<Exemplar> exemplares;

    @OneToMany(mappedBy = "livro")
    private List<Reserva> reservas;

    @Size(min = 1, max = 255, message = "campo deve ter entre 1 e 255 caracteres")
    @NotBlank(message = "campo nao deve ficar em branco")
    @Column(nullable = false)
    private String nome;

    @Size(min = 0, max = 20, message = "o campo deve ter entre 0 e 20 cacaracteres")
    private String isbn;

    @Size(min = 0, max = 255, message = "o campo deve ter 0 e 255 caracteres")
    private String editora;

    @NotNull(message = "campo nao deve ser nulo")
    private String capa;

    @Column(columnDefinition = "TEXT")
    private String sinopse;

    public Livro(Categoria categoria, String nome, String isbn, String editora, String capa, String sinopse) {
        this.categoria = categoria;
        this.nome = nome;
        this.isbn = isbn;
        this.editora = editora;
        this.capa = capa;
        this.sinopse = sinopse;
    }
}

