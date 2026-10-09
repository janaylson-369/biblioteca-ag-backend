package br.gov.biblioteca_ag_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_livros")
public class Livro {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long idLivro;

    @Column(nullable = false, unique = true)
    private String titulo;

    @Column(nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String editora;

    @Column(nullable = false)
    private Date anoPublicacao;

    @Column(nullable = false)
    private String capa;

    @Column(nullable = false)
    private String sinopse;
}
