package br.gov.biblioteca_ag_backend.repository;

import br.gov.biblioteca_ag_backend.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    List<Livro> findByAutores_Id(Long idAutor);

    List<Livro> findByCategoria_Id(Long idCategoria);
}
