package br.gov.biblioteca_ag_backend.model;

import br.gov.biblioteca_ag_backend.enums.TipoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Entity()
@Table(name = "tb_usuarios")
public class Usuario implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "campo não pode ficar em branco.")
    @Size(min = 1, max = 100, message = "campo deve ficar entre 1 a 100 caracteres.")
    private String nome;

    @Size(min = 1, max = 12, message = "campo deve ficar entre 1 e 12 caracteres.")
    private String telefone;

    @Column(unique = true)
    @Email(message = "campo deve ser preenchido com um email.")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "campo deve ser preenchido")
    @Size(min = 5 ,message = "campo ter no minimo 5 caracteres")
    private String senha_hash;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotBlank(message = "campo nao pode ser vazio")
    private TipoUsuario tipoUsuario;

    public Usuario(String nome, String telefone, String email, String senha_hash, TipoUsuario tipoUsuario) {
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.senha_hash = senha_hash;
        this.tipoUsuario = tipoUsuario;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (tipoUsuario == TipoUsuario.ADMIN) return List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_FUNCIONARIO"),
                new SimpleGrantedAuthority("ROLE_USUARIO"));

        if (tipoUsuario == TipoUsuario.BIBLIOTECARIO) return List.of(
                new SimpleGrantedAuthority("ROLE_FUNCIONARIO"),
                new SimpleGrantedAuthority("ROLE_USUARIO"));

        return List.of(new SimpleGrantedAuthority("ROLE_USUARIO"));
    }

    @Override
    public @Nullable String getPassword() {
        return this.senha_hash;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
