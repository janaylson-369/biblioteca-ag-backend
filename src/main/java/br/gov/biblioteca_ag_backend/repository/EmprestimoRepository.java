package br.gov.biblioteca_ag_backend.repository;

import br.gov.biblioteca_ag_backend.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
}
