package br.gov.biblioteca_ag_backend.repository;

import br.gov.biblioteca_ag_backend.model.DadosLeitor;
import br.gov.biblioteca_ag_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DadosLeitorRepository extends JpaRepository<DadosLeitor, Long> {
    Usuario findByUsuario_Id(Long idUsuario);
}
