package br.gov.biblioteca_ag_backend.repository;

import br.gov.biblioteca_ag_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    //UserDetails findByLogin(String email);

    List<Usuario> findByNomeStartingWith(String nome);
}
