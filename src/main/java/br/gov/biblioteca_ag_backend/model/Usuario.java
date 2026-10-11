package br.gov.biblioteca_ag_backend.model;

import br.gov.biblioteca_ag_backend.enums.TipoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "usuario")
    private DadosLeitor dadosLeitor;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Emprestimo> emprestimos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Reserva> reservas = new ArrayList<>();

    @Column(nullable = false)
    @NotBlank(message = "campo não pode ficar em branco.")
    @Size(min = 1, max = 100, message = "campo deve ficar entre 1 a 100 caracteres.")
    private String nome;

    @Size(min = 1, max = 20, message = "campo deve ficar entre 1 e 20 caracteres.")
    private String telefone;

    @Column(unique = true)
    @Email(message = "campo deve ser preenchido com um email.")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "campo deve ser preenchido")
    private String senha_hash;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario = TipoUsuario.LEITOR;

    public Usuario(String nome, String telefone, String email, String senha_hash) {
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.senha_hash = senha_hash;
    }
}
