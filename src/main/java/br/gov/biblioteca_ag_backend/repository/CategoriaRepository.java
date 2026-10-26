package br.gov.biblioteca_ag_backend.repository;

import br.gov.biblioteca_ag_backend.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
