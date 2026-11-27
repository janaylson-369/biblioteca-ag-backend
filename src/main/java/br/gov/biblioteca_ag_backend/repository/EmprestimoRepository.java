package br.gov.biblioteca_ag_backend.repository;

import br.gov.biblioteca_ag_backend.enums.StatusEmprestimo;
import br.gov.biblioteca_ag_backend.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByExemplar_Id(Long idExemplar);

    List<Emprestimo> findByUsuario_Id(Long idUsuario);

    List<Emprestimo> findByStatus(StatusEmprestimo statusEmprestimo);

    boolean existsByUsuario_Id(Long idUsuario);

    boolean existsByExemplar_Id(Long idExemplar);

    boolean existsByUsuario_IdAndExemplar_Livro_IdAndStatus(Long idUsuario, Long idLivro, StatusEmprestimo statusEmprestimo);

    boolean existsByExemplar_Livro_Id(Long livroId);

    long countByUsuario_IdAndStatus(
            Long idUsuario,
            StatusEmprestimo status
    );

    List<Emprestimo> findByStatusAndDataPrevisaoDevolucaoBefore(StatusEmprestimo statusEmprestimo, LocalDateTime localDateTime);
}
