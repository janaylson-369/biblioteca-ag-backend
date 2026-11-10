package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.emprestimo.CriarEmprestimoDTO;
import br.gov.biblioteca_ag_backend.dto.emprestimo.CriarEmprestimoComReservaDTO;
import br.gov.biblioteca_ag_backend.dto.emprestimo.EmprestimoCompletoDTO;
import br.gov.biblioteca_ag_backend.dto.emprestimo.EmprestimoRespostaDTO;

import br.gov.biblioteca_ag_backend.enums.StatusEmprestimo;
import br.gov.biblioteca_ag_backend.enums.StatusExemplar;
import br.gov.biblioteca_ag_backend.enums.StatusReserva;

import br.gov.biblioteca_ag_backend.exceptions.ResourceAlreadyExistsException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceNotFoundException;
import br.gov.biblioteca_ag_backend.exceptions.RequestValidationException;

import br.gov.biblioteca_ag_backend.model.Emprestimo;
import br.gov.biblioteca_ag_backend.model.Exemplar;
import br.gov.biblioteca_ag_backend.model.Reserva;
import br.gov.biblioteca_ag_backend.model.Usuario;

import br.gov.biblioteca_ag_backend.repository.EmprestimoRepository;
import br.gov.biblioteca_ag_backend.repository.ExemplarRepository;
import br.gov.biblioteca_ag_backend.repository.ReservaRepository;
import br.gov.biblioteca_ag_backend.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ExemplarRepository exemplarRepository;
    private final ReservaRepository reservaRepository;

    public EmprestimoService(
            EmprestimoRepository emprestimoRepository,
            UsuarioRepository usuarioRepository,
            ExemplarRepository exemplarRepository,
            ReservaRepository reservaRepository
    ) {
        this.emprestimoRepository = emprestimoRepository;
        this.usuarioRepository = usuarioRepository;
        this.exemplarRepository = exemplarRepository;
        this.reservaRepository = reservaRepository;
    }

    @Transactional(readOnly = true)
    public List<EmprestimoRespostaDTO> listarTodos() {
        return emprestimoRepository.findAll()
                .stream()
                .map(EmprestimoRespostaDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmprestimoCompletoDTO buscarPorId(Long id) {
        Emprestimo e = obterEmprestimo(id);

        return EmprestimoCompletoDTO.fromEntity(e);
    }

    @Transactional(readOnly = true)
    public List<EmprestimoCompletoDTO> listarPorUsuario(Long idUsuario) {

        obterUsuario(idUsuario);

        return emprestimoRepository.findByUsuario_Id(idUsuario)
                .stream()
                .map(EmprestimoCompletoDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmprestimoCompletoDTO> listarEmAndamento() {
        return emprestimoRepository.findByStatus(
                        StatusEmprestimo.EM_ANDAMENTO
                )
                .stream()
                .map(EmprestimoCompletoDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmprestimoCompletoDTO> listarAtrasados() {

        LocalDateTime agora = LocalDateTime.now(
                ZoneId.of("America/Fortaleza")
        );

        return emprestimoRepository
                .findByStatusAndDataPrevisaoDevolucaoBefore(
                        StatusEmprestimo.EM_ANDAMENTO,
                        agora
                )
                .stream()
                .map(EmprestimoCompletoDTO::fromEntity)
                .toList();
    }

    @Transactional
    public EmprestimoCompletoDTO cadastrarSemReserva(
            CriarEmprestimoDTO dto
    ) {

        validarDiasEmprestimo(dto.diasEmprestimo());

        Usuario us = obterUsuario(dto.id_usuario());

        validarUsuarioParaEmprestimo(us);

        validarLimiteEmprestimos(us.getId());

        Exemplar ex = obterExemplar(dto.id_exemplar());

        if (!ex.estaDisponivel()) {
            throw new RequestValidationException(
                    "id exemplar: " + dto.id_exemplar(),
                    "O exemplar informado não está disponível para empréstimo."
            );
        }

        if (emprestimoRepository
                .existsByUsuario_IdAndExemplar_Livro_IdAndStatus(
                        us.getId(),
                        ex.getLivro().getId(),
                        StatusEmprestimo.EM_ANDAMENTO
                )) {

            throw new ResourceAlreadyExistsException(
                    "emprestimo existente",
                    "O usuário já possui um empréstimo em andamento deste livro."
            );
        }

        LocalDateTime agora = LocalDateTime.now(
                ZoneId.of("America/Fortaleza")
        );

        Emprestimo e = new Emprestimo();

        e.setUsuario(us);
        e.setExemplar(ex);
        e.setDataEmprestimo(agora);
        e.setDataPrevisaoDevolucao(
                agora.plusDays(dto.diasEmprestimo())
        );

        ex.atualizarStatus(StatusExemplar.EMPRESTADO);

        Emprestimo salvo = emprestimoRepository.save(e);

        return EmprestimoCompletoDTO.fromEntity(salvo);
    }

    @Transactional
    public EmprestimoCompletoDTO cadastrarComReserva(
            CriarEmprestimoComReservaDTO dto
    ) {

        validarDiasEmprestimo(dto.diasEmprestimo());

        Reserva re = reservaRepository.findById(dto.id_reserva())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "id reserva",
                        String.format(
                                "Reserva de id %d inexistente.",
                                dto.id_reserva()
                        )
                ));

        if (re.getStatus() != StatusReserva.ATIVA) {
            throw new RequestValidationException(
                    "id reserva: " + dto.id_reserva(),
                    "A reserva informada não está ativa."
            );
        }

        Usuario us = re.getUsuario();

        validarUsuarioParaEmprestimo(us);

        validarLimiteEmprestimos(us.getId());

        Exemplar ex = obterExemplar(dto.id_exemplar());

        if (!Objects.equals(
                ex.getLivro().getId(),
                re.getLivro().getId()
        )) {
            throw new RequestValidationException(
                    "id exemplar: " + dto.id_exemplar(),
                    "O exemplar informado não pertence ao livro da reserva."
            );
        }

        if (!ex.estaDisponivel()) {
            throw new RequestValidationException(
                    "id exemplar: " + dto.id_exemplar(),
                    "O exemplar informado não está disponível para empréstimo."
            );
        }

        if (emprestimoRepository
                .existsByUsuario_IdAndExemplar_Livro_IdAndStatus(
                        us.getId(),
                        ex.getLivro().getId(),
                        StatusEmprestimo.EM_ANDAMENTO
                )) {

            throw new ResourceAlreadyExistsException(
                    "id usuario: " + us.getId(),
                    "O usuário já possui um empréstimo em andamento deste livro."
            );
        }

        LocalDateTime agora = LocalDateTime.now(
                ZoneId.of("America/Fortaleza")
        );

        Emprestimo e = new Emprestimo();

        re.marcarAtendida();

        e.setReserva(re);
        e.setUsuario(us);
        e.setExemplar(ex);
        e.setDataEmprestimo(agora);
        e.setDataPrevisaoDevolucao(
                agora.plusDays(dto.diasEmprestimo())
        );

        ex.atualizarStatus(StatusExemplar.EMPRESTADO);

        Emprestimo salvo = emprestimoRepository.save(e);

        return EmprestimoCompletoDTO.fromEntity(salvo);
    }

    @Transactional
    public EmprestimoCompletoDTO devolver(Long id) {

        Emprestimo e = obterEmprestimo(id);

        if (e.getStatus() != StatusEmprestimo.EM_ANDAMENTO) {
            throw new RequestValidationException(
                    "id emprestimo: " + id,
                    "Somente empréstimos em andamento podem ser devolvidos."
            );
        }

        e.registrarDevolucao();

        return EmprestimoCompletoDTO.fromEntity(e);
    }

    @Transactional
    public EmprestimoCompletoDTO renovar(
            Long id,
            long diasAdicionais
    ) {

        Emprestimo e = obterEmprestimo(id);

        if (e.getStatus() != StatusEmprestimo.EM_ANDAMENTO) {
            throw new RequestValidationException(
                    "id emprestimo: " + id,
                    "Somente empréstimos em andamento podem ser renovados."
            );
        }

        e.renovar(diasAdicionais);

        return EmprestimoCompletoDTO.fromEntity(e);
    }

    // ========================================
    private void validarUsuarioParaEmprestimo(Usuario usuario) {

        if (usuario.getDadosLeitor() == null) {
            throw new RequestValidationException(
                    "dados leitor inexistentes",
                    String.format(
                            "usuario de id %d não possui cadastro de dados de leitor e não pode realizar empréstimos.",
                            usuario.getId()
                    )
            );
        }

        if (Boolean.TRUE.equals(
                usuario.getDadosLeitor().getBloqueio()
        )) {
            throw new RequestValidationException(
                    "usuario bloqueado",
                    String.format(
                            "usuario de id %d está bloqueado e não pode realizar empréstimos.",
                            usuario.getId()
                    )
            );
        }
    }

    private void validarLimiteEmprestimos(Long idUsuario) {

        long quantidade = emprestimoRepository
                .countByUsuario_IdAndStatus(
                        idUsuario,
                        StatusEmprestimo.EM_ANDAMENTO
                );

        if (quantidade >= 3) {
            throw new RequestValidationException(
                    "limite de emprestimos",
                    "O usuário já possui o limite de 3 empréstimos em andamento."
            );
        }
    }

    private void validarDiasEmprestimo(long dias) {

        if (dias < 3 || dias > 30) {
            throw new RequestValidationException(
                    "periodo de emprestimo invalido",
                    "O período de empréstimo deve ser entre 3 e 30 dias."
            );
        }
    }

    private Emprestimo obterEmprestimo(Long id) {
        return emprestimoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "id empréstimo",
                        String.format(
                                "Empréstimo de id %d inexistente.",
                                id
                        )
                ));
    }

    private Usuario obterUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "id usuário",
                        String.format(
                                "Usuário de id %d inexistente.",
                                id
                        )
                ));
    }

    private Exemplar obterExemplar(Long id) {
        return exemplarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "id exemplar",
                        String.format(
                                "Exemplar de id %d inexistente.",
                                id
                        )
                ));
    }
}