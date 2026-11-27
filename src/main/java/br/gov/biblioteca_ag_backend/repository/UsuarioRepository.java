package br.gov.biblioteca_ag_backend.repository;

import br.gov.biblioteca_ag_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByNomeStartingWith(String nome);
}
