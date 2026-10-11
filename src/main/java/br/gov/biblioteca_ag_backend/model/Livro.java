package br.gov.biblioteca_ag_backend.model;

import br.gov.biblioteca_ag_backend.enums.StatusExemplar;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;

import java.util.HashSet;
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
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @ManyToMany(mappedBy = "livros", fetch = FetchType.LAZY)
    private Set<Autor> autores = new HashSet<>();

    @OneToMany(mappedBy = "livro", fetch = FetchType.LAZY)
    private List<Exemplar> exemplares;

    @OneToMany(mappedBy = "livro", fetch = FetchType.LAZY)
    private List<Reserva> reservas;

    @Size(min = 1, max = 255, message = "campo deve ter entre 1 e 255 caracteres")
    @NotBlank(message = "campo nao deve ficar em branco")
    @Column(nullable = false)
    private String nome;

    @Column(unique = true)
    @Size(min = 0, max = 20, message = "o campo deve ter entre 0 e 20 cacaracteres")
    private String isbn;

    @Size(min = 0, max = 255, message = "o campo deve ter 0 e 255 caracteres")
    private String editora;

    @Column(nullable = false)
    @NotNull(message = "campo nao deve ser nulo")
    private String capa;

    @Column(columnDefinition = "TEXT")
    private String sinopse;

    public Livro(Categoria categoria, String nome, String isbn, String capa, String editora, String sinopse, Set<Autor> autores) {
        this.categoria = categoria;
        this.nome = nome;
        this.isbn = isbn;
        this.capa = capa;
        this.editora = editora;
        this.sinopse = sinopse;
        this.autores = autores;
    }

    public void adicionarAutor(Autor autor) {
        getAutores().add(autor);
    }

    public int obterExemplaresDisponiveis() {
        int n = 0;
        for (Exemplar ex : getExemplares()) {
            if (ex.getStatus() == StatusExemplar.DISPONIVEL) {
                n++;
            }
        }
        return n;
    }
}

