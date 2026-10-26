package br.gov.biblioteca_ag_backend.repository;

import br.gov.biblioteca_ag_backend.enums.StatusReserva;
import br.gov.biblioteca_ag_backend.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUsuario_Id(Long idUsuario);

    List<Reserva> findByLivro_Id(Long idLivro);

    List<Reserva> findByStatus(StatusReserva statusReserva);
}
