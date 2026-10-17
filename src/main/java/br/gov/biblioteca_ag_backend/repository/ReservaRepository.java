package br.gov.biblioteca_ag_backend.repository;

import br.gov.biblioteca_ag_backend.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}
