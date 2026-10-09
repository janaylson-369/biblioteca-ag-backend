package br.gov.biblioteca_ag_backend.repository;

import br.gov.biblioteca_ag_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
