package br.gov.biblioteca_ag_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_dados_leitor")
public class DadosLeitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", unique = true, nullable = false)
    private Usuario usuario;

    @NotNull
    @Past
    private LocalDate dataNascimento;

    @Column(nullable = false)
    private Boolean bloqueio = false;

    @Column(name = "motivo_bloqueio")
    private String motivoBloqueio;

    @Column(name = "bloqueio_ate")
    private LocalDate bloqueioAte;


    @Size(min = 0, max = 255, message = "campo deve ter entre 0 e 255 caractereres.")
    private String logradouro;

    @Size(min = 0, max = 255, message = "campo deve ter entre 0 e 255 caractereres.")
    private String numero;

    @Size(min = 0, max = 255, message = "campo deve ter entre 0 e 255 caractereres.")
    private String complemento;

    @Size(min = 0, max = 255, message = "campo deve ter entre 0 e 255 caractereres.")
    private String bairro;

    @Size(min = 0,max = 255, message = "campo deve ser preenchido.")
    private String nomeResponsavel;

    @Size(min = 0,max = 255, message = "campo deve ser preenchido.")
    private String contatoResponsavel;

    @Column(name = "consentimento_responsavel_em")
    private LocalDateTime consentimentoResponsavelEm;

    public DadosLeitor(Usuario usuario, LocalDate dataNascimento, String logradouro, String numero, String complemento, String bairro) {
        this.usuario = usuario;
        this.dataNascimento = dataNascimento;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
    }


    public void aplicarBloqueio(String motivo, LocalDate ate) {
        if (motivo == null || motivo.isBlank()){
            throw new IllegalStateException("O motivo do bloqueio é obrigatório.");
        }
        if (ate == null || ate.isBefore(LocalDate.now())){
            throw new IllegalArgumentException("A data final do bloqueio deve ser hoje ou uma data futura.");
        }

        this.motivoBloqueio = motivo;
        this.bloqueioAte = ate;

    }

    public void removerBloqueio() {
        this.motivoBloqueio = null;
        this.bloqueioAte = null;
    }

    public boolean estaBloqueado() {
        if (this.bloqueioAte == null){
            return false;
        }
        return !LocalDate.now().isAfter(this.bloqueioAte);
    }

    public boolean isMenorDeDozeAnos(){
        if (this.dataNascimento == null){
            return false;
        }
        int idade = Period.between(this.dataNascimento,LocalDate.now()).getYears();
        return idade < 12;
    }
}
