package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.reserva.CriarReservaDTO;
import br.gov.biblioteca_ag_backend.dto.reserva.ReservaCompletoDTO;
import br.gov.biblioteca_ag_backend.dto.reserva.ReservaRespostaDTO;
import br.gov.biblioteca_ag_backend.enums.StatusReserva;
import br.gov.biblioteca_ag_backend.exceptions.RequestValidationException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceAlreadyExistsException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceNotFoundException;
import br.gov.biblioteca_ag_backend.model.DadosLeitor;
import br.gov.biblioteca_ag_backend.model.Livro;
import br.gov.biblioteca_ag_backend.model.Reserva;
import br.gov.biblioteca_ag_backend.model.Usuario;
import br.gov.biblioteca_ag_backend.repository.LivroRepository;
import br.gov.biblioteca_ag_backend.repository.ReservaRepository;
import br.gov.biblioteca_ag_backend.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private ReservaService reservaService;

    private Usuario usuario;
    private Livro livro;
    private DadosLeitor dadosLeitor;
    private Reserva reserva;

    @BeforeEach
    void setUp() {

        dadosLeitor = new DadosLeitor();
        dadosLeitor.setBloqueio(false);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João");
        usuario.setDadosLeitor(dadosLeitor);

        livro = new Livro();
        livro.setId(10L);
        livro.setTitulo("Clean Code");

        reserva = new Reserva();
        reserva.setId(100L);
        reserva.setUsuario(usuario);
        reserva.setLivro(livro);
        reserva.setStatus(StatusReserva.ATIVA);
    }

    // ----------------------------------------------------
    // LISTAR TODOS
    // ----------------------------------------------------

    @Test
    void deveListarTodasAsReservas() {

        when(reservaRepository.findAll())
                .thenReturn(List.of(reserva));

        List<ReservaRespostaDTO> resultado =
                reservaService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals(100L, resultado.get(0).id());
        assertEquals(1L, resultado.get(0).id_usuario());
        assertEquals(10L, resultado.get(0).id_livro());

        verify(reservaRepository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremReservas() {

        when(reservaRepository.findAll())
                .thenReturn(List.of());

        List<ReservaRespostaDTO> resultado =
                reservaService.listarTodos();

        assertTrue(resultado.isEmpty());

        verify(reservaRepository).findAll();
    }

    // ----------------------------------------------------
    // BUSCAR POR ID
    // ----------------------------------------------------

    @Test
    void deveBuscarReservaPorId() {

        when(reservaRepository.findById(100L))
                .thenReturn(Optional.of(reserva));

        ReservaCompletoDTO resultado =
                reservaService.buscarPorId(100L);

        assertNotNull(resultado);
        assertEquals(100L, resultado.id());

        verify(reservaRepository).findById(100L);
    }

    @Test
    void deveLancarExcecaoQuandoReservaNaoExistir() {

        when(reservaRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> reservaService.buscarPorId(100L)
        );

        verify(reservaRepository).findById(100L);
    }

    // ----------------------------------------------------
    // CADASTRAR
    // ----------------------------------------------------

    @Test
    void deveCadastrarReservaComSucesso() {

        CriarReservaDTO dto =
                new CriarReservaDTO(10L, 1L);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(livroRepository.findById(10L))
                .thenReturn(Optional.of(livro));

        when(reservaRepository
                .countByStatusAndUsuario_Id(StatusReserva.ATIVA, 1L))
                .thenReturn(0L);

        when(reservaRepository
                .existsByUsuario_IdAndLivro_IdAndStatus(
                        1L, 10L, StatusReserva.ATIVA
                ))
                .thenReturn(false);

        when(reservaRepository.save(any(Reserva.class)))
                .thenAnswer(invocation -> {
                    Reserva salva = invocation.getArgument(0);
                    salva.setId(100L);
                    return salva;
                });

        ReservaRespostaDTO resultado =
                reservaService.cadastrar(dto);

        assertNotNull(resultado);
        assertEquals(100L, resultado.id());
        assertEquals(1L, resultado.id_usuario());
        assertEquals(10L, resultado.id_livro());
        assertEquals(StatusReserva.ATIVA, resultado.statusReserva());
        assertNotNull(resultado.dataReserva());

        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {

        CriarReservaDTO dto =
                new CriarReservaDTO(10L, 1L);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> reservaService.cadastrar(dto)
        );

        verify(livroRepository, never()).findById(any());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoPossuirDadosLeitor() {

        usuario.setDadosLeitor(null);

        CriarReservaDTO dto =
                new CriarReservaDTO(10L, 1L);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        assertThrows(
                RequestValidationException.class,
                () -> reservaService.cadastrar(dto)
        );

        verify(livroRepository, never()).findById(any());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioEstiverBloqueado() {

        dadosLeitor.setBloqueio(true);

        CriarReservaDTO dto =
                new CriarReservaDTO(10L, 1L);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        assertThrows(
                RequestValidationException.class,
                () -> reservaService.cadastrar(dto)
        );

        verify(livroRepository, never()).findById(any());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoLivroNaoExistir() {

        CriarReservaDTO dto =
                new CriarReservaDTO(10L, 1L);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(livroRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> reservaService.cadastrar(dto)
        );

        verify(reservaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioAtingirLimiteDeReservas() {

        CriarReservaDTO dto =
                new CriarReservaDTO(10L, 1L);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(livroRepository.findById(10L))
                .thenReturn(Optional.of(livro));

        when(reservaRepository
                .countByStatusAndUsuario_Id(StatusReserva.ATIVA, 1L))
                .thenReturn(3L);

        assertThrows(
                RequestValidationException.class,
                () -> reservaService.cadastrar(dto)
        );

        verify(reservaRepository, never())
                .existsByUsuario_IdAndLivro_IdAndStatus(
                        any(), any(), any()
                );

        verify(reservaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoReservaAtivaJaExistir() {

        CriarReservaDTO dto =
                new CriarReservaDTO(10L, 1L);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(livroRepository.findById(10L))
                .thenReturn(Optional.of(livro));

        when(reservaRepository
                .countByStatusAndUsuario_Id(StatusReserva.ATIVA, 1L))
                .thenReturn(1L);

        when(reservaRepository
                .existsByUsuario_IdAndLivro_IdAndStatus(
                        1L, 10L, StatusReserva.ATIVA
                ))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> reservaService.cadastrar(dto)
        );

        verify(reservaRepository, never()).save(any());
    }

    // ----------------------------------------------------
    // CANCELAR
    // ----------------------------------------------------

    @Test
    void deveCancelarReservaAtivaComSucesso() {

        when(reservaRepository.findById(100L))
                .thenReturn(Optional.of(reserva));

        ReservaRespostaDTO resultado =
                reservaService.cancelar(100L);

        assertNotNull(resultado);
        assertEquals(StatusReserva.CANCELADA, resultado.statusReserva());

        verify(reservaRepository).findById(100L);

        // O Dirty Checking dispensa o save()
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoCancelarReservaNaoAtiva() {

        reserva.setStatus(StatusReserva.ATENDIDA);

        when(reservaRepository.findById(100L))
                .thenReturn(Optional.of(reserva));

        assertThrows(
                RequestValidationException.class,
                () -> reservaService.cancelar(100L)
        );

        verify(reservaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoCancelarReservaInexistente() {

        when(reservaRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> reservaService.cancelar(100L)
        );

        verify(reservaRepository, never()).save(any());
    }
}