package br.gov.biblioteca_ag_backend.repository;

import br.gov.biblioteca_ag_backend.enums.StatusReserva;
import br.gov.biblioteca_ag_backend.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUsuario_Id(Long idUsuario);

    boolean existsByUsuario_Id(Long idUsuario);

    List<Reserva> findByLivro_Id(Long idLivro);

    List<Reserva> findByStatus(StatusReserva statusReserva);

    long countByStatusAndUsuario_Id(StatusReserva statusReserva, Long idUsuario);

    boolean existsByUsuario_IdAndLivro_IdAndStatus(
            Long usuarioId,
            Long livroId,
            StatusReserva status
    );

    boolean existsByLivro_Id(Long livroId);
}