package br.gov.biblioteca_ag_backend.model;

import br.gov.biblioteca_ag_backend.enums.StatusReserva;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_livro", nullable = false)
    private Livro livro;

    @OneToOne(mappedBy = "reserva")
    private Emprestimo emprestimo;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime dataReserva = LocalDateTime.now();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusReserva status = StatusReserva.ATIVA;

    public Reserva(Livro livro, Usuario usuario) {
        this.livro = livro;
        this.usuario = usuario;
    }

    public void cancelar() {
        setStatus(StatusReserva.CANCELADA);
    }

    public void marcarAtendida() {
        setStatus(StatusReserva.ATENDIDA);
    }

    public boolean verificarExpiracao() {
        return (status == StatusReserva.EXPIRADA);
    }
}
