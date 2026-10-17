package br.gov.biblioteca_ag_backend.repository;

import br.gov.biblioteca_ag_backend.model.Exemplar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExemplarRepository extends JpaRepository<Exemplar, Long> {
}
