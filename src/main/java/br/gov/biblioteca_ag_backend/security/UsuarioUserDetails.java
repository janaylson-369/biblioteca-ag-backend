package br.gov.biblioteca_ag_backend.security;

import br.gov.biblioteca_ag_backend.enums.TipoUsuario;
import br.gov.biblioteca_ag_backend.model.Usuario;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UsuarioUserDetails implements UserDetails {

    private Usuario usuario;

    public UsuarioUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (usuario.getTipoUsuario() == TipoUsuario.ADMIN) return List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_FUNCIONARIO"),
                new SimpleGrantedAuthority("ROLE_USUARIO"));

        if (usuario.getTipoUsuario() == TipoUsuario.BIBLIOTECARIO) return List.of(
                new SimpleGrantedAuthority("ROLE_FUNCIONARIO"),
                new SimpleGrantedAuthority("ROLE_USUARIO"));

        return List.of(new SimpleGrantedAuthority("ROLE_USUARIO"));
    }

    @Override
    public @Nullable String getPassword() {
        return this.usuario.getSenhaHash();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return usuario.isAtivo();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.usuario.isAtivo();
    }

    // Permite recuperar a entidade original no Controller/Service quando necessário
    public Usuario getUsuario() {
        return usuario;
    }

}
