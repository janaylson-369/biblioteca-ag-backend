package br.gov.biblioteca_ag_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_dados_usuarios")
public class DadosLeitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Past
    private LocalDate dataNascimento;

    private Boolean bloqueio;

    private String logradouro;

    private String numero;

    private String complemento;

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
