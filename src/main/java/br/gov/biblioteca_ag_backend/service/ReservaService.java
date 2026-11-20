package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.reserva.CriarReservaDTO;
import br.gov.biblioteca_ag_backend.dto.reserva.ReservaCompletoDTO;
import br.gov.biblioteca_ag_backend.dto.reserva.ReservaRespostaDTO;
import br.gov.biblioteca_ag_backend.exceptions.RequestValidationException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceAlreadyExistsException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceNotFoundException;
import br.gov.biblioteca_ag_backend.model.Livro;
import br.gov.biblioteca_ag_backend.model.Reserva;
import br.gov.biblioteca_ag_backend.model.Usuario;
import br.gov.biblioteca_ag_backend.enums.StatusReserva;
import br.gov.biblioteca_ag_backend.repository.LivroRepository;
import br.gov.biblioteca_ag_backend.repository.ReservaRepository;
import br.gov.biblioteca_ag_backend.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;

    public ReservaService(
            ReservaRepository reservaRepository,
            UsuarioRepository usuarioRepository,
            LivroRepository livroRepository
    ) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.livroRepository = livroRepository;
    }

    @Transactional(readOnly = true)
    public List<ReservaRespostaDTO> listarTodos() {
        return reservaRepository.findAll()
                .stream()
                .map(ReservaRespostaDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservaCompletoDTO buscarPorId(Long id) {
        Reserva r = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "id reserva",
                        String.format("reserva de id %d inexistente", id)
                ));

        return ReservaCompletoDTO.fromEntity(r);
    }

    @Transactional
    public ReservaRespostaDTO cadastrar(CriarReservaDTO dto) {

        Usuario u = usuarioRepository.findById(dto.id_usuario())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "id usuario",
                        String.format("usuario de id %d inexistente", dto.id_usuario())
                ));

        if (u.getDadosLeitor() == null) {
            throw new RequestValidationException(
                    "dados leitor inexistentes",
                    String.format(
                            "usuario de id %d não possui cadastro de dados de leitor e não pode realizar reservas.",
                            u.getId()
                    )
            );
        }

        if (Boolean.TRUE.equals(u.getDadosLeitor().getBloqueio())) {
            throw new RequestValidationException(
                    "usuario bloqueado",
                    String.format(
                            "usuario de id %d está bloqueado e não pode realizar reservas.",
                            u.getId()
                    )
            );
        }

        Livro l = livroRepository.findById(dto.id_livro())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "id livro",
                        String.format("livro de id %d inexistente", dto.id_livro())
                ));

        long qtdReservasAtivas = reservaRepository
                .countByStatusAndUsuario_Id(StatusReserva.ATIVA, u.getId());

        if (qtdReservasAtivas >= 3) {
            throw new RequestValidationException(
                    "limite reserva para usuario",
                    String.format(
                            "usuario de id %d já possui o limite de reservas ativas.",
                            u.getId()
                    )
            );
        }

        boolean reservaExistente = reservaRepository
                .existsByUsuario_IdAndLivro_IdAndStatus(
                        u.getId(),
                        l.getId(),
                        StatusReserva.ATIVA
                );

        if (reservaExistente) {
            throw new ResourceAlreadyExistsException(
                    "reserva ja existente ativa",
                    String.format(
                            "reserva com usuario %d e livro %d ativa ja existe",
                            u.getId(),
                            l.getId()
                    )
            );
        }

        Reserva r = new Reserva();
        r.setLivro(l);
        r.setUsuario(u);
        r.setStatus(StatusReserva.ATIVA);
        r.setDataReserva(
                LocalDateTime.now(ZoneId.of("America/Fortaleza"))
        );

        Reserva salvo = reservaRepository.save(r);

        return ReservaRespostaDTO.fromEntity(salvo);
    }

    @Transactional
    public ReservaRespostaDTO cancelar(Long idReserva) {

        Reserva r = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "id reserva",
                        String.format("reserva de id %d inexistente", idReserva)
                ));

        if (r.getStatus() != StatusReserva.ATIVA) {
            throw new RequestValidationException(
                    "impossivel cancelar",
                    String.format(
                            "reserva de id %d nao pode ser cancelada pois nao está ativa",
                            idReserva
                    )
            );
        }

        r.cancelar();

        return ReservaRespostaDTO.fromEntity(r);
    }
}