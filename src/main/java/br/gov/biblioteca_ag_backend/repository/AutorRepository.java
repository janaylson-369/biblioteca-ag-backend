package br.gov.biblioteca_ag_backend.repository;

import br.gov.biblioteca_ag_backend.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepository extends JpaRepository<Livro, Long> {
}
