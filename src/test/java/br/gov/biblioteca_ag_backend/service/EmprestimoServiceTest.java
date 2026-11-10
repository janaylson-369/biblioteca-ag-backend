
package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.emprestimo.*;
import br.gov.biblioteca_ag_backend.enums.*;
import br.gov.biblioteca_ag_backend.model.*;
import br.gov.biblioteca_ag_backend.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ExemplarRepository exemplarRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private EmprestimoService emprestimoService;

    private Usuario usuario;
    private Livro livro;
    private Exemplar exemplar;
    private DadosLeitor dadosLeitor;

    @BeforeEach
    void configurar() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");

        dadosLeitor = new DadosLeitor();
        dadosLeitor.setBloqueio(false);
        usuario.setDadosLeitor(dadosLeitor);

        livro = new Livro();
        livro.setId(1L);
        livro.setTitulo("Livro Teste");

        exemplar = new Exemplar();
        exemplar.setId(1L);
        exemplar.setLivro(livro);
        exemplar.setStatus(StatusExemplar.DISPONIVEL);
    }

    // -------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------

    private Emprestimo criarEmprestimo() {
        Emprestimo emprestimo = new Emprestimo(
                usuario,
                exemplar,
                LocalDateTime.now().plusDays(10)
        );

        emprestimo.setId(1L);
        emprestimo.setStatus(StatusEmprestimo.EM_ANDAMENTO);

        return emprestimo;
    }

    private Reserva criarReserva() {
        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setUsuario(usuario);
        reserva.setLivro(livro);
        reserva.setStatus(StatusReserva.ATIVA);

        return reserva;
    }

    private void configurarCadastroSemReserva() {
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(emprestimoRepository.countByUsuario_IdAndStatus(
                1L, StatusEmprestimo.EM_ANDAMENTO
        )).thenReturn(0L);

        when(exemplarRepository.findById(1L))
                .thenReturn(Optional.of(exemplar));

        when(emprestimoRepository
                .existsByUsuario_IdAndExemplar_Livro_IdAndStatus(
                        1L, 1L, StatusEmprestimo.EM_ANDAMENTO
                )).thenReturn(false);

        when(emprestimoRepository.save(any(Emprestimo.class)))
                .thenAnswer(invocation -> {
                    Emprestimo e = invocation.getArgument(0);
                    e.setId(1L);
                    return e;
                });
    }

    private void configurarCadastroComReserva() {
        Reserva reserva = criarReserva();

        when(reservaRepository.findById(1L))
                .thenReturn(Optional.of(reserva));

        when(emprestimoRepository.countByUsuario_IdAndStatus(
                1L, StatusEmprestimo.EM_ANDAMENTO
        )).thenReturn(0L);

        when(exemplarRepository.findById(1L))
                .thenReturn(Optional.of(exemplar));

        when(emprestimoRepository
                .existsByUsuario_IdAndExemplar_Livro_IdAndStatus(
                        1L, 1L, StatusEmprestimo.EM_ANDAMENTO
                )).thenReturn(false);

        when(emprestimoRepository.save(any(Emprestimo.class)))
                .thenAnswer(invocation -> {
                    Emprestimo e = invocation.getArgument(0);
                    e.setId(1L);
                    return e;
                });
    }

    // -------------------------------------------------------
    // LISTAR TODOS
    // -------------------------------------------------------

    @Test
    void listarTodos_DeveRetornarLista() {
        when(emprestimoRepository.findAll())
                .thenReturn(List.of(criarEmprestimo()));

        assertEquals(1, emprestimoService.listarTodos().size());

        verify(emprestimoRepository).findAll();
    }

    @Test
    void listarTodos_DeveRetornarListaVazia() {
        when(emprestimoRepository.findAll())
                .thenReturn(List.of());

        assertTrue(emprestimoService.listarTodos().isEmpty());
    }

    // -------------------------------------------------------
    // BUSCAR POR ID
    // -------------------------------------------------------

    @Test
    void buscarPorId_DeveRetornarEmprestimo() {
        when(emprestimoRepository.findById(1L))
                .thenReturn(Optional.of(criarEmprestimo()));

        assertNotNull(emprestimoService.buscarPorId(1L));
    }

    @Test
    void buscarPorId_DeveLancarExcecaoQuandoNaoEncontrado() {
        when(emprestimoRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> emprestimoService.buscarPorId(1L));
    }

    // -------------------------------------------------------
    // LISTAR POR USUÁRIO
    // -------------------------------------------------------

    @Test
    void listarPorUsuario_DeveRetornarEmprestimos() {
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(emprestimoRepository.findByUsuario_Id(1L))
                .thenReturn(List.of(criarEmprestimo()));

        assertEquals(1, emprestimoService.listarPorUsuario(1L).size());
    }

    @Test
    void listarPorUsuario_DeveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> emprestimoService.listarPorUsuario(1L));
    }

    // -------------------------------------------------------
    // LISTAR EM ANDAMENTO
    // -------------------------------------------------------

    @Test
    void listarEmAndamento_DeveRetornarEmprestimos() {
        when(emprestimoRepository.findByStatus(
                StatusEmprestimo.EM_ANDAMENTO
        )).thenReturn(List.of(criarEmprestimo()));

        assertEquals(1, emprestimoService.listarEmAndamento().size());
    }

    // -------------------------------------------------------
    // LISTAR ATRASADOS
    // -------------------------------------------------------

    @Test
    void listarAtrasados_DeveConsultarEmprestimosAtrasados() {
        when(emprestimoRepository
                .findByStatusAndDataPrevisaoDevolucaoBefore(
                        eq(StatusEmprestimo.EM_ANDAMENTO),
                        any(LocalDateTime.class)
                )).thenReturn(List.of(criarEmprestimo()));

        assertEquals(1, emprestimoService.listarAtrasados().size());

        verify(emprestimoRepository)
                .findByStatusAndDataPrevisaoDevolucaoBefore(
                        eq(StatusEmprestimo.EM_ANDAMENTO),
                        any(LocalDateTime.class)
                );
    }

    // -------------------------------------------------------
    // CADASTRAR SEM RESERVA
    // -------------------------------------------------------

    @Test
    void cadastrarSemReserva_DeveCadastrarEmprestimo() {
        configurarCadastroSemReserva();

        CriarEmprestimoDTO dto =
                new CriarEmprestimoDTO(1L, 1L, 7);

        assertNotNull(emprestimoService.cadastrarSemReserva(dto));

        assertEquals(StatusExemplar.EMPRESTADO, exemplar.getStatus());

        verify(emprestimoRepository).save(any(Emprestimo.class));
    }

    @Test
    void cadastrarSemReserva_DeveRejeitarDiasInvalidos() {
        CriarEmprestimoDTO dto =
                new CriarEmprestimoDTO(1L, 1L, 2);

        assertThrows(RuntimeException.class,
                () -> emprestimoService.cadastrarSemReserva(dto));

        verify(emprestimoRepository, never()).save(any());
    }

    @Test
    void cadastrarSemReserva_DeveRejeitarUsuarioInexistente() {
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.empty());

        CriarEmprestimoDTO dto =
                new CriarEmprestimoDTO(1L, 1L, 7);

        assertThrows(RuntimeException.class,
                () -> emprestimoService.cadastrarSemReserva(dto));
    }

    @Test
    void cadastrarSemReserva_DeveRejeitarUsuarioSemDadosLeitor() {
        usuario.setDadosLeitor(null);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        CriarEmprestimoDTO dto =
                new CriarEmprestimoDTO(1L, 1L, 7);

        assertThrows(RuntimeException.class,
                () -> emprestimoService.cadastrarSemReserva(dto));
    }

    @Test
    void cadastrarSemReserva_DeveRejeitarUsuarioBloqueado() {
        dadosLeitor.setBloqueio(true);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        CriarEmprestimoDTO dto =
                new CriarEmprestimoDTO(1L, 1L, 7);

        assertThrows(RuntimeException.class,
                () -> emprestimoService.cadastrarSemReserva(dto));
    }

    @Test
    void cadastrarSemReserva_DeveRejeitarLimiteDeEmprestimos() {
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(emprestimoRepository.countByUsuario_IdAndStatus(
                1L, StatusEmprestimo.EM_ANDAMENTO
        )).thenReturn(3L);

        CriarEmprestimoDTO dto =
                new CriarEmprestimoDTO(1L, 1L, 7);

        assertThrows(RuntimeException.class,
                () -> emprestimoService.cadastrarSemReserva(dto));
    }

    @Test
    void cadastrarSemReserva_DeveRejeitarExemplarInexistente() {
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(emprestimoRepository.countByUsuario_IdAndStatus(
                1L, StatusEmprestimo.EM_ANDAMENTO
        )).thenReturn(0L);

        when(exemplarRepository.findById(1L))
                .thenReturn(Optional.empty());

        CriarEmprestimoDTO dto =
                new CriarEmprestimoDTO(1L, 1L, 7);

        assertThrows(RuntimeException.class,
                () -> emprestimoService.cadastrarSemReserva(dto));
    }

    @Test
    void cadastrarSemReserva_DeveRejeitarExemplarIndisponivel() {
        exemplar.setStatus(StatusExemplar.EMPRESTADO);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(emprestimoRepository.countByUsuario_IdAndStatus(
                1L, StatusEmprestimo.EM_ANDAMENTO
        )).thenReturn(0L);

        when(exemplarRepository.findById(1L))
                .thenReturn(Optional.of(exemplar));

        CriarEmprestimoDTO dto =
                new CriarEmprestimoDTO(1L, 1L, 7);

        assertThrows(RuntimeException.class,
                () -> emprestimoService.cadastrarSemReserva(dto));
    }

    @Test
    void cadastrarSemReserva_DeveRejeitarLivroJaEmprestadoPeloUsuario() {
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(emprestimoRepository.countByUsuario_IdAndStatus(
                1L, StatusEmprestimo.EM_ANDAMENTO
        )).thenReturn(0L);

        when(exemplarRepository.findById(1L))
                .thenReturn(Optional.of(exemplar));

        when(emprestimoRepository
                .existsByUsuario_IdAndExemplar_Livro_IdAndStatus(
                        1L, 1L, StatusEmprestimo.EM_ANDAMENTO
                )).thenReturn(true);

        CriarEmprestimoDTO dto =
                new CriarEmprestimoDTO(1L, 1L, 7);

        assertThrows(RuntimeException.class,
                () -> emprestimoService.cadastrarSemReserva(dto));
    }

    // -------------------------------------------------------
    // CADASTRAR COM RESERVA
    // -------------------------------------------------------

    @Test
    void cadastrarComReserva_DeveCadastrarEmprestimo() {
        configurarCadastroComReserva();

        CriarEmprestimoComReservaDTO dto =
                new CriarEmprestimoComReservaDTO(1L, 1L, 7L);

        assertNotNull(emprestimoService.cadastrarComReserva(dto));

        assertEquals(StatusExemplar.EMPRESTADO, exemplar.getStatus());

        verify(emprestimoRepository).save(any(Emprestimo.class));
    }

    @Test
    void cadastrarComReserva_DeveRejeitarReservaInexistente() {
        when(reservaRepository.findById(1L))
                .thenReturn(Optional.empty());

        CriarEmprestimoComReservaDTO dto =
                new CriarEmprestimoComReservaDTO(1L, 1L, 7L);

        assertThrows(RuntimeException.class,
                () -> emprestimoService.cadastrarComReserva(dto));
    }

    @Test
    void cadastrarComReserva_DeveRejeitarReservaNaoAtiva() {
        Reserva reserva = criarReserva();
        reserva.setStatus(StatusReserva.CANCELADA);

        when(reservaRepository.findById(1L))
                .thenReturn(Optional.of(reserva));

        CriarEmprestimoComReservaDTO dto =
                new CriarEmprestimoComReservaDTO(1L, 1L, 7L);

        assertThrows(RuntimeException.class,
                () -> emprestimoService.cadastrarComReserva(dto));
    }

    @Test
    void cadastrarComReserva_DeveRejeitarUsuarioSemDadosLeitor() {
        Reserva reserva = criarReserva();
        reserva.getUsuario().setDadosLeitor(null);

        when(reservaRepository.findById(1L))
                .thenReturn(Optional.of(reserva));

        CriarEmprestimoComReservaDTO dto =
                new CriarEmprestimoComReservaDTO(1L, 1L, 7L);

        assertThrows(RuntimeException.class,
                () -> emprestimoService.cadastrarComReserva(dto));
    }

    @Test
    void cadastrarComReserva_DeveRejeitarUsuarioBloqueado() {
        dadosLeitor.setBloqueio(true);

        when(reservaRepository.findById(1L))
                .thenReturn(Optional.of(criarReserva()));

        CriarEmprestimoComReservaDTO dto =
                new CriarEmprestimoComReservaDTO(1L, 1L, 7L);

        assertThrows(RuntimeException.class,
                () -> emprestimoService.cadastrarComReserva(dto));
    }

    @Test
    void cadastrarComReserva_DeveRejeitarLimiteDeEmprestimos() {
        when(reservaRepository.findById(1L))
                .thenReturn(Optional.of(criarReserva()));

        when(emprestimoRepository.countByUsuario_IdAndStatus(
                1L, StatusEmprestimo.EM_ANDAMENTO
        )).thenReturn(3L);

        CriarEmprestimoComReservaDTO dto =
                new CriarEmprestimoComReservaDTO(1L, 1L, 7L);

        assertThrows(RuntimeException.class,
                () -> emprestimoService.cadastrarComReserva(dto));
    }

    @Test
    void cadastrarComReserva_DeveRejeitarExemplarInexistente() {
        when(reservaRepository.findById(1L))
                .thenReturn(Optional.of(criarReserva()));

        when(emprestimoRepository.countByUsuario_IdAndStatus(
                1L, StatusEmprestimo.EM_ANDAMENTO
        )).thenReturn(0L);

        when(exemplarRepository.findById(1L))
                .thenReturn(Optional.empty());

        CriarEmprestimoComReservaDTO dto =
                new CriarEmprestimoComReservaDTO(1L, 1L, 7L);

        assertThrows(RuntimeException.class,
                () -> emprestimoService.cadastrarComReserva(dto));
    }

    @Test
    void cadastrarComReserva_DeveRejeitarExemplarDeOutroLivro() {
        Livro outroLivro = new Livro();
        outroLivro.setId(2L);
        outroLivro.setTitulo("Outro Livro");

        exemplar.setLivro(outroLivro);

        when(reservaRepository.findById(1L))
                .thenReturn(Optional.of(criarReserva()));

        when(emprestimoRepository.countByUsuario_IdAndStatus(
                1L, StatusEmprestimo.EM_ANDAMENTO
        )).thenReturn(0L);

        when(exemplarRepository.findById(1L))
                .thenReturn(Optional.of(exemplar));

        CriarEmprestimoComReservaDTO dto =
                new CriarEmprestimoComReservaDTO(1L, 1L, 7L);

        assertThrows(RuntimeException.class,
                () -> emprestimoService.cadastrarComReserva(dto));
    }

    @Test
    void cadastrarComReserva_DeveRejeitarExemplarIndisponivel() {
        exemplar.setStatus(StatusExemplar.EMPRESTADO);

        when(reservaRepository.findById(1L))
                .thenReturn(Optional.of(criarReserva()));

        when(emprestimoRepository.countByUsuario_IdAndStatus(
                1L, StatusEmprestimo.EM_ANDAMENTO
        )).thenReturn(0L);

        when(exemplarRepository.findById(1L))
                .thenReturn(Optional.of(exemplar));

        CriarEmprestimoComReservaDTO dto =
                new CriarEmprestimoComReservaDTO(1L, 1L, 7L);

        assertThrows(RuntimeException.class,
                () -> emprestimoService.cadastrarComReserva(dto));
    }

    @Test
    void cadastrarComReserva_DeveRejeitarLivroJaEmprestado() {
        when(reservaRepository.findById(1L))
                .thenReturn(Optional.of(criarReserva()));

        when(emprestimoRepository.countByUsuario_IdAndStatus(
                1L, StatusEmprestimo.EM_ANDAMENTO
        )).thenReturn(0L);

        when(exemplarRepository.findById(1L))
                .thenReturn(Optional.of(exemplar));

        when(emprestimoRepository
                .existsByUsuario_IdAndExemplar_Livro_IdAndStatus(
                        1L, 1L, StatusEmprestimo.EM_ANDAMENTO
                )).thenReturn(true);

        CriarEmprestimoComReservaDTO dto =
                new CriarEmprestimoComReservaDTO(1L, 1L, 7L);

        assertThrows(RuntimeException.class,
                () -> emprestimoService.cadastrarComReserva(dto));
    }

    // -------------------------------------------------------
    // DEVOLVER
    // -------------------------------------------------------

    @Test
    void devolver_DeveRegistrarDevolucao() {
        Emprestimo emprestimo = criarEmprestimo();

        when(emprestimoRepository.findById(1L))
                .thenReturn(Optional.of(emprestimo));

        assertNotNull(emprestimoService.devolver(1L));

        assertEquals(StatusEmprestimo.CONCLUIDO, emprestimo.getStatus());
        assertEquals(StatusExemplar.DISPONIVEL, exemplar.getStatus());
        assertNotNull(emprestimo.getDataDevolucaoReal());
    }

    @Test
    void devolver_DeveRejeitarEmprestimoInexistente() {
        when(emprestimoRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> emprestimoService.devolver(1L));
    }

    @Test
    void devolver_DeveRejeitarEmprestimoJaConcluido() {
        Emprestimo emprestimo = criarEmprestimo();
        emprestimo.setStatus(StatusEmprestimo.CONCLUIDO);

        when(emprestimoRepository.findById(1L))
                .thenReturn(Optional.of(emprestimo));

        assertThrows(RuntimeException.class,
                () -> emprestimoService.devolver(1L));
    }

    // -------------------------------------------------------
    // RENOVAR
    // -------------------------------------------------------

    @Test
    void renovar_DeveProrrogarDataDeDevolucao() {
        Emprestimo emprestimo = criarEmprestimo();

        LocalDateTime previsaoOriginal =
                emprestimo.getDataPrevisaoDevolucao();

        when(emprestimoRepository.findById(1L))
                .thenReturn(Optional.of(emprestimo));

        assertNotNull(emprestimoService.renovar(1L, 5));

        assertEquals(
                previsaoOriginal.plusDays(5),
                emprestimo.getDataPrevisaoDevolucao()
        );
    }

    @Test
    void renovar_DeveRejeitarEmprestimoInexistente() {
        when(emprestimoRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> emprestimoService.renovar(1L, 5));
    }

    @Test
    void renovar_DeveRejeitarEmprestimoConcluido() {
        Emprestimo emprestimo = criarEmprestimo();
        emprestimo.setStatus(StatusEmprestimo.CONCLUIDO);

        when(emprestimoRepository.findById(1L))
                .thenReturn(Optional.of(emprestimo));

        assertThrows(RuntimeException.class,
                () -> emprestimoService.renovar(1L, 5));
    }

    @Test
    void renovar_DeveRejeitarQuantidadeDeDiasNegativa() {
        Emprestimo emprestimo = criarEmprestimo();

        when(emprestimoRepository.findById(1L))
                .thenReturn(Optional.of(emprestimo));

        assertThrows(RuntimeException.class,
                () -> emprestimoService.renovar(1L, -5));
    }

    @Test
    void renovar_DeveRejeitarQuantidadeDeDiasIgualAZero() {
        Emprestimo emprestimo = criarEmprestimo();

        when(emprestimoRepository.findById(1L))
                .thenReturn(Optional.of(emprestimo));

        assertThrows(RuntimeException.class,
                () -> emprestimoService.renovar(1L, 0));
    }
}
