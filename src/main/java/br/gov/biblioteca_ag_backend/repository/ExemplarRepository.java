package br.gov.biblioteca_ag_backend.repository;

import br.gov.biblioteca_ag_backend.enums.StatusExemplar;
import br.gov.biblioteca_ag_backend.model.Exemplar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExemplarRepository extends JpaRepository<Exemplar, Long> {
    List<Exemplar> findByLivro_Id(Long idLivro);

    List<Exemplar> findByStatus(StatusExemplar statusExemplar);

    List<Exemplar> findByLivro_id(Long livroId);

    boolean existsByLivro_Id(Long livroId);

    boolean existsByNTombamento(String nTombamento);

    boolean existsByNTombamentoAndIdNot(String nTombamento, Long id);

    @Query("""
    SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
    FROM Exemplar e
    WHERE e.nTombamento = :nTombamento
    """)
    boolean existeTombamento(String nTombamento);

    @Query("""
    SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
    FROM Exemplar e
    WHERE e.nTombamento = :nTombamento
    AND e.id <> :id
    """)
    boolean existeTombamentoEmOutroExemplar(
            String nTombamento,
            Long id
    );
}
