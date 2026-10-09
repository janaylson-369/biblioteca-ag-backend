package br.gov.biblioteca_ag_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_dados_leitor")
public class DadosLeitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_usuario", unique = true)
    private Usuario usuario;

    @NotNull
    @Past
    private LocalDate dataNascimento;

    private Boolean bloqueio;

    @Size(min = 0, max = 255, message = "campo deve ter entre 0 e 255 caractereres.")
    private String logradouro;

    @Size(min = 0, max = 255, message = "campo deve ter entre 0 e 255 caractereres.")
    private String numero;

    @Size(min = 0, max = 255, message = "campo deve ter entre 0 e 255 caractereres.")
    private String complemento;

    @Size(min = 0, max = 255, message = "campo deve ter entre 0 e 255 caractereres.")
    private String bairro;

    public DadosLeitor(LocalDate dataNascimento, Boolean bloqueio, String logradouro, String numero, String complemento, String bairro) {
        this.dataNascimento = dataNascimento;
        this.bloqueio = bloqueio;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
    }

    public void aplicarBloqueio() {
        setBloqueio(true);
    }

    public void removerBloqueio() {
        setBloqueio(false);
    }

    public Boolean estaBloqueado() {
        return bloqueio;
    }
}
