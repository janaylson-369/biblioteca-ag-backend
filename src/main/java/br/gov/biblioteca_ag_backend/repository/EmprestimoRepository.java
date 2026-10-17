package br.gov.biblioteca_ag_backend.repository;

import br.gov.biblioteca_ag_backend.enums.StatusEmprestimo;
import br.gov.biblioteca_ag_backend.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByExemplar_Id(Long idExemplar);

    List<Emprestimo> findByUsuario_Id(Long idUsuario);

    List<Emprestimo> findByStatus(StatusEmprestimo statusEmprestimo);
}
