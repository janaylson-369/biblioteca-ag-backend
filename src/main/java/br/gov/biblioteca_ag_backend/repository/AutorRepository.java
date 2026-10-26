package br.gov.biblioteca_ag_backend.repository;

import br.gov.biblioteca_ag_backend.model.Autor;
import br.gov.biblioteca_ag_backend.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    List<Autor> findByLivros_Id(Long idLivro);
}
