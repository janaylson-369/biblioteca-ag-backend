package br.gov.biblioteca_ag_backend.model;

import br.gov.biblioteca_ag_backend.enums.TipoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @OneToOne(mappedBy = "usuario", fetch = FetchType.EAGER)
    private DadosLeitor dadosLeitor;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Emprestimo> emprestimos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Reserva> reservas = new ArrayList<>();

    @Column(nullable = false)
    @NotBlank(message = "Campo não pode ficar em branco.")
    @Size(min = 1, max = 100, message = "Campo deve ficar entre 1 e 100 caracteres.")
    private String nome;

    @Size(min = 1, max = 20, message = "Campo deve ficar entre 1 e 20 caracteres.")
    private String telefone;

    @Column(unique = true)
    @Email(message = "Campo deve ser preenchido com um e-mail válido.")
    private String email;

    @Column(name = "senha_hash", nullable = false)
    @NotBlank(message = "Campo deve ser preenchido.")
    private String senhaHash;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario = TipoUsuario.LEITOR;

    @Column(nullable = false)
    private boolean ativo = false;

    @Column(name = "aceite_privacidade_em")
    private LocalDateTime aceitePrivacidadeEm;

    @Column(name = "versao_politica_aceita", length = 20)
    private String versaoPoliticaAceita;

    @Column(name = "ultimo_acesso_em")
    private LocalDateTime ultimoAcessoEm;

    @Column(name = "anonimizado_em")
    private LocalDateTime anonimizadoEm;

    public Usuario(String nome, String telefone, String email, String senhaHash) {
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.senhaHash = senhaHash;
        this.ativo = false;
    }

    public void registrarAceitePrivacidade(String versaoAtualPolitica) {
        if (versaoAtualPolitica == null || versaoAtualPolitica.isBlank()) {
            throw new IllegalArgumentException("A versão da política de privacidade é obrigatória.");
        }
        this.versaoPoliticaAceita = versaoAtualPolitica;
        this.aceitePrivacidadeEm = LocalDateTime.now();
    }

    public boolean autenticar(String informarSenha) {
        if (!this.ativo || informarSenha == null) return false;
        return this.senhaHash.equals(informarSenha);
    }

    public void registrarAcesso() {
        this.ultimoAcessoEm = LocalDateTime.now();
    }

    public void alterarSenha(String novaSenhaHash) {
        if (novaSenhaHash == null || novaSenhaHash.isBlank()) {
            throw new IllegalArgumentException("Nova senha não pode estar vazia.");
        }
        this.senhaHash = novaSenhaHash;
    }

    public void anonimizarTitular() {
        String idAnonimo = UUID.randomUUID().toString().substring(0, 8);
        this.nome = "Titular Anonimizado";
        this.telefone = null;
        this.email = "anonimo_" + idAnonimo + "@biblioteca.local";
        this.senhaHash = UUID.randomUUID().toString();
        this.ativo = false;
        this.anonimizadoEm = LocalDateTime.now();
    }
}
