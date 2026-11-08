
package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.usuario.*;
import br.gov.biblioteca_ag_backend.enums.TipoUsuario;
import br.gov.biblioteca_ag_backend.exceptions.RequestValidationException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceNotFoundException;
import br.gov.biblioteca_ag_backend.model.DadosLeitor;
import br.gov.biblioteca_ag_backend.model.Usuario;
import br.gov.biblioteca_ag_backend.repository.DadosLeitorRepository;
import br.gov.biblioteca_ag_backend.repository.EmprestimoRepository;
import br.gov.biblioteca_ag_backend.repository.ReservaRepository;
import br.gov.biblioteca_ag_backend.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private DadosLeitorRepository dadosLeitorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(
                "Gabriel",
                "83999999999",
                "gabriel@email.com",
                "senhaHash"
        );

        usuario.setId(1L);
        usuario.setTipoUsuario(TipoUsuario.LEITOR);
    }

    // =========================
    // LISTAR TODOS
    // =========================

    @Test
    void deveListarTodosUsuarios() {

        Usuario outro = new Usuario(
                "Maria",
                "83988888888",
                "maria@email.com",
                "hash"
        );

        outro.setId(2L);

        when(usuarioRepository.findAll())
                .thenReturn(List.of(usuario, outro));

        List<UsuarioRespostaDTO> resultado =
                usuarioService.listarTodos();

        assertEquals(2, resultado.size());
        assertEquals("Gabriel", resultado.get(0).nome());
        assertEquals("Maria", resultado.get(1).nome());

        verify(usuarioRepository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremUsuarios() {

        when(usuarioRepository.findAll())
                .thenReturn(List.of());

        List<UsuarioRespostaDTO> resultado =
                usuarioService.listarTodos();

        assertTrue(resultado.isEmpty());
    }

    // =========================
    // BUSCAR POR ID
    // =========================

    @Test
    void deveBuscarUsuarioPorId() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        UsuarioResumoDTO resultado =
                usuarioService.buscarPorId(1L);

        assertEquals(1L, resultado.id());
        assertEquals("Gabriel", resultado.nome());
        assertEquals("gabriel@email.com", resultado.email());

        verify(usuarioRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarUsuarioInexistente() {

        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.buscarPorId(99L)
        );
    }

    // =========================
    // CADASTRAR
    // =========================

    @Test
    void deveCadastrarUsuarioComoLeitor() {

        CriarUsuarioDTO dto = new CriarUsuarioDTO(
                "Gabriel",
                "83999999999",
                "gabriel@email.com",
                "senha123"
        );

        when(usuarioRepository.existsByEmail(dto.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(dto.senha_normal()))
                .thenReturn("senhaCodificada");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario salvo = invocation.getArgument(0);
                    salvo.setId(1L);
                    return salvo;
                });

        UsuarioRespostaDTO resultado =
                usuarioService.cadastrar(dto);

        assertEquals(1L, resultado.id());
        assertEquals("Gabriel", resultado.nome());
        assertEquals("gabriel@email.com", resultado.email());

        verify(passwordEncoder).encode("senha123");

        verify(usuarioRepository).save(argThat(u ->
                u.getNome().equals("Gabriel") &&
                        u.getEmail().equals("gabriel@email.com") &&
                        u.getSenha_hash().equals("senhaCodificada") &&
                        u.getTipoUsuario() == TipoUsuario.LEITOR
        ));
    }

    @Test
    void naoDeveCadastrarUsuarioComEmailDuplicado() {

        CriarUsuarioDTO dto = new CriarUsuarioDTO(
                "Gabriel",
                "83999999999",
                "gabriel@email.com",
                "senha123"
        );

        when(usuarioRepository.existsByEmail(dto.email()))
                .thenReturn(true);

        assertThrows(
                RequestValidationException.class,
                () -> usuarioService.cadastrar(dto)
        );

        verify(usuarioRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void deveCadastrarUsuarioSemEmail() {

        CriarUsuarioDTO dto = new CriarUsuarioDTO(
                "Gabriel",
                "83999999999",
                null,
                "senha123"
        );

        when(passwordEncoder.encode(dto.senha_normal()))
                .thenReturn("senhaCodificada");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario salvo = invocation.getArgument(0);
                    salvo.setId(1L);
                    return salvo;
                });

        UsuarioRespostaDTO resultado =
                usuarioService.cadastrar(dto);

        assertNull(resultado.email());

        verify(usuarioRepository, never()).existsByEmail(any());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    // =========================
    // ATUALIZAR COMPLETO - PUT
    // =========================

    @Test
    void deveAtualizarUsuarioCompleto() {

        AtualizarUsuarioCompletoDTO dto =
                new AtualizarUsuarioCompletoDTO(
                        "Gabriel Atualizado",
                        "83988888888",
                        "novo@email.com"
                );

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByEmailAndIdNot(
                dto.email(), 1L
        )).thenReturn(false);

        UsuarioRespostaDTO resultado =
                usuarioService.atualizarCompleto(1L, dto);

        assertEquals("Gabriel Atualizado", usuario.getNome());
        assertEquals("83988888888", usuario.getTelefone());
        assertEquals("novo@email.com", usuario.getEmail());

        assertEquals("Gabriel Atualizado", resultado.nome());

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void naoDeveAtualizarCompletoComEmailDuplicado() {

        AtualizarUsuarioCompletoDTO dto =
                new AtualizarUsuarioCompletoDTO(
                        "Gabriel",
                        "83999999999",
                        "outro@email.com"
                );

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByEmailAndIdNot(
                dto.email(), 1L
        )).thenReturn(true);

        assertThrows(
                RequestValidationException.class,
                () -> usuarioService.atualizarCompleto(1L, dto)
        );

        assertEquals("Gabriel", usuario.getNome());
    }

    @Test
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {

        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        AtualizarUsuarioCompletoDTO dto =
                new AtualizarUsuarioCompletoDTO(
                        "Gabriel",
                        null,
                        null
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.atualizarCompleto(99L, dto)
        );
    }

    // =========================
    // ATUALIZAR PARCIALMENTE - PATCH
    // =========================

    @Test
    void deveAtualizarSomenteCamposInformados() {

        AtualizarUsuarioDTO dto = new AtualizarUsuarioDTO(
                "Novo Nome",
                null,
                null
        );

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        UsuarioRespostaDTO resultado =
                usuarioService.atualizar(1L, dto);

        assertEquals("Novo Nome", usuario.getNome());

        assertEquals("83999999999", usuario.getTelefone());
        assertEquals("gabriel@email.com", usuario.getEmail());

        assertEquals("Novo Nome", resultado.nome());

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveAtualizarEmailNoPatch() {

        AtualizarUsuarioDTO dto = new AtualizarUsuarioDTO(
                null,
                null,
                "novo@email.com"
        );

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByEmailAndIdNot(
                dto.email(), 1L
        )).thenReturn(false);

        usuarioService.atualizar(1L, dto);

        assertEquals("novo@email.com", usuario.getEmail());
    }

    @Test
    void naoDeveAtualizarEmailComDuplicidadeNoPatch() {

        AtualizarUsuarioDTO dto = new AtualizarUsuarioDTO(
                null,
                null,
                "outro@email.com"
        );

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByEmailAndIdNot(
                dto.email(), 1L
        )).thenReturn(true);

        assertThrows(
                RequestValidationException.class,
                () -> usuarioService.atualizar(1L, dto)
        );

        assertEquals("gabriel@email.com", usuario.getEmail());
    }

    @Test
    void deveManterTodosOsCamposQuandoPatchReceberNulos() {

        AtualizarUsuarioDTO dto =
                new AtualizarUsuarioDTO(null, null, null);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        UsuarioRespostaDTO resultado =
                usuarioService.atualizar(1L, dto);

        assertEquals("Gabriel", resultado.nome());
        assertEquals("83999999999", resultado.telefone());
        assertEquals("gabriel@email.com", resultado.email());

        verify(usuarioRepository, never())
                .existsByEmailAndIdNot(any(), any());

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarUsuarioInexistenteNoPatch() {

        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        AtualizarUsuarioDTO dto =
                new AtualizarUsuarioDTO("Novo Nome", null, null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.atualizar(99L, dto)
        );
    }

    // =========================
    // EXCLUIR
    // =========================

    @Test
    void deveExcluirUsuarioSemRelacionamentos() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(emprestimoRepository.existsByUsuario_Id(1L))
                .thenReturn(false);

        when(reservaRepository.existsByUsuario_Id(1L))
                .thenReturn(false);

        usuarioService.excluir(1L);

        verify(usuarioRepository).delete(usuario);
        verify(dadosLeitorRepository, never()).delete(any());
    }

    @Test
    void deveExcluirDadosLeitorAntesDeExcluirUsuario() {

        DadosLeitor dadosLeitor = mock(DadosLeitor.class);
        usuario.setDadosLeitor(dadosLeitor);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(emprestimoRepository.existsByUsuario_Id(1L))
                .thenReturn(false);

        when(reservaRepository.existsByUsuario_Id(1L))
                .thenReturn(false);

        usuarioService.excluir(1L);

        var ordem = inOrder(
                dadosLeitorRepository,
                usuarioRepository
        );

        ordem.verify(dadosLeitorRepository).delete(dadosLeitor);
        ordem.verify(usuarioRepository).delete(usuario);
    }

    @Test
    void naoDeveExcluirUsuarioComEmprestimos() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(emprestimoRepository.existsByUsuario_Id(1L))
                .thenReturn(true);

        when(reservaRepository.existsByUsuario_Id(1L))
                .thenReturn(false);

        assertThrows(
                RequestValidationException.class,
                () -> usuarioService.excluir(1L)
        );

        verify(usuarioRepository, never()).delete(any());
        verify(dadosLeitorRepository, never()).delete(any());
    }

    @Test
    void naoDeveExcluirUsuarioComReservas() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(emprestimoRepository.existsByUsuario_Id(1L))
                .thenReturn(false);

        when(reservaRepository.existsByUsuario_Id(1L))
                .thenReturn(true);

        assertThrows(
                RequestValidationException.class,
                () -> usuarioService.excluir(1L)
        );

        verify(usuarioRepository, never()).delete(any());
        verify(dadosLeitorRepository, never()).delete(any());
    }

    @Test
    void deveLancarExcecaoAoExcluirUsuarioInexistente() {

        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.excluir(99L)
        );

        verify(usuarioRepository, never()).delete(any());
    }
}