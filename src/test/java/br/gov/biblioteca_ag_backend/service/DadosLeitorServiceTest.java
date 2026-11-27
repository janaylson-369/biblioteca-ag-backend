package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.dadosLeitor.AtualizarDadosLeitorDTO;
import br.gov.biblioteca_ag_backend.dto.dadosLeitor.CriarDadosLeitorDTO;
import br.gov.biblioteca_ag_backend.dto.dadosLeitor.DadosLeitorCompletoDTO;
import br.gov.biblioteca_ag_backend.exceptions.ResourceAlreadyExistsException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceNotFoundException;
import br.gov.biblioteca_ag_backend.model.DadosLeitor;
import br.gov.biblioteca_ag_backend.model.Usuario;
import br.gov.biblioteca_ag_backend.repository.DadosLeitorRepository;
import br.gov.biblioteca_ag_backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DadosLeitorServiceTest {

    @Mock
    private DadosLeitorRepository dadosLeitorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private DadosLeitorService dadosLeitorService;

    // --------------------------------------------------
    // CADASTRAR
    // --------------------------------------------------

    @Test
    void cadastrarDadosComSucesso() {

        Long id = 1L;

        Usuario usuario = criarUsuario(id);

        CriarDadosLeitorDTO dto = new CriarDadosLeitorDTO(
                LocalDate.of(2000, 5, 10),
                "Rua A",
                "123",
                "Casa",
                "Centro"
        );

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        when(dadosLeitorRepository.existsByUsuario_Id(id))
                .thenReturn(false);

        when(dadosLeitorRepository.save(any(DadosLeitor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DadosLeitorCompletoDTO resposta =
                dadosLeitorService.cadastrarDados(id, dto);

        assertNotNull(resposta);
        assertEquals(id, resposta.id_usuario());
        assertEquals("Gabriel", resposta.nome());

        assertEquals(
                LocalDate.of(2000, 5, 10),
                resposta.dadosLeitor().dataNascimento()
        );

        assertEquals("Rua A", resposta.dadosLeitor().logradouro());

        assertNotNull(usuario.getDadosLeitor());

        verify(usuarioRepository).findById(id);
        verify(dadosLeitorRepository).existsByUsuario_Id(id);
        verify(dadosLeitorRepository).save(any(DadosLeitor.class));
    }

    @Test
    void cadastrarDadosUsuarioInexistente() {

        Long id = 1L;

        CriarDadosLeitorDTO dto = new CriarDadosLeitorDTO(
                LocalDate.of(2000, 5, 10),
                "Rua A",
                "123",
                null,
                "Centro"
        );

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> dadosLeitorService.cadastrarDados(id, dto)
        );

        verify(dadosLeitorRepository, never())
                .save(any(DadosLeitor.class));
    }

    @Test
    void cadastrarDadosQuandoJaExistemDadosLeitor() {

        Long id = 1L;

        Usuario usuario = criarUsuario(id);

        CriarDadosLeitorDTO dto = new CriarDadosLeitorDTO(
                LocalDate.of(2000, 5, 10),
                "Rua A",
                "123",
                null,
                "Centro"
        );

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        when(dadosLeitorRepository.existsByUsuario_Id(id))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> dadosLeitorService.cadastrarDados(id, dto)
        );

        verify(dadosLeitorRepository, never())
                .save(any(DadosLeitor.class));
    }

    // --------------------------------------------------
    // BUSCAR
    // --------------------------------------------------

    @Test
    void buscarPorUsuarioIdComSucesso() {

        Long id = 1L;

        Usuario usuario = criarUsuario(id);

        DadosLeitor dados = criarDadosLeitor(usuario);

        usuario.setDadosLeitor(dados);

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        DadosLeitorCompletoDTO resposta =
                dadosLeitorService.buscarPorUsuarioId(id);

        assertNotNull(resposta);
        assertEquals(id, resposta.id_usuario());
        assertEquals("Gabriel", resposta.nome());

        assertEquals(
                LocalDate.of(2000, 5, 10),
                resposta.dadosLeitor().dataNascimento()
        );

        assertEquals("Centro", resposta.dadosLeitor().bairro());

        verify(usuarioRepository).findById(id);
    }

    @Test
    void buscarPorUsuarioIdQuandoUsuarioNaoExiste() {

        Long id = 1L;

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> dadosLeitorService.buscarPorUsuarioId(id)
        );
    }

    @Test
    void buscarPorUsuarioIdQuandoNaoExistemDadosLeitor() {

        Long id = 1L;

        Usuario usuario = criarUsuario(id);

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        assertThrows(
                ResourceNotFoundException.class,
                () -> dadosLeitorService.buscarPorUsuarioId(id)
        );
    }

    // --------------------------------------------------
    // ATUALIZAR
    // --------------------------------------------------

    @Test
    void atualizarDadosComSucesso() {

        Long id = 1L;

        Usuario usuario = criarUsuario(id);

        DadosLeitor dados = criarDadosLeitor(usuario);

        usuario.setDadosLeitor(dados);

        AtualizarDadosLeitorDTO dto = new AtualizarDadosLeitorDTO(
                LocalDate.of(2001, 8, 15),
                "Rua B",
                "456",
                "Apartamento",
                "Bairro Novo"
        );

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        DadosLeitorCompletoDTO resposta =
                dadosLeitorService.atualizar(id, dto);

        assertNotNull(resposta);

        assertEquals(
                LocalDate.of(2001, 8, 15),
                dados.getDataNascimento()
        );

        assertEquals("Rua B", dados.getLogradouro());
        assertEquals("456", dados.getNumero());
        assertEquals("Apartamento", dados.getComplemento());
        assertEquals("Bairro Novo", dados.getBairro());

        verify(usuarioRepository).findById(id);

        verify(dadosLeitorRepository, never())
                .save(any(DadosLeitor.class));
    }

    @Test
    void atualizarDadosParcialmente() {

        Long id = 1L;

        Usuario usuario = criarUsuario(id);

        DadosLeitor dados = criarDadosLeitor(usuario);

        usuario.setDadosLeitor(dados);

        AtualizarDadosLeitorDTO dto = new AtualizarDadosLeitorDTO(
                null,
                "Rua Atualizada",
                null,
                null,
                null
        );

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        DadosLeitorCompletoDTO resposta =
                dadosLeitorService.atualizar(id, dto);

        assertNotNull(resposta);

        assertEquals("Rua Atualizada", dados.getLogradouro());

        // Os campos não enviados devem permanecer iguais.
        assertEquals("123", dados.getNumero());
        assertEquals("Casa", dados.getComplemento());
        assertEquals("Centro", dados.getBairro());

        assertEquals(
                LocalDate.of(2000, 5, 10),
                dados.getDataNascimento()
        );
    }

    @Test
    void atualizarDadosQuandoUsuarioNaoExiste() {

        Long id = 1L;

        AtualizarDadosLeitorDTO dto = new AtualizarDadosLeitorDTO(
                null,
                "Rua B",
                null,
                null,
                null
        );

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> dadosLeitorService.atualizar(id, dto)
        );
    }

    @Test
    void atualizarDadosQuandoUsuarioNaoPossuiDadosLeitor() {

        Long id = 1L;

        Usuario usuario = criarUsuario(id);

        AtualizarDadosLeitorDTO dto = new AtualizarDadosLeitorDTO(
                null,
                "Rua B",
                null,
                null,
                null
        );

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        assertThrows(
                ResourceNotFoundException.class,
                () -> dadosLeitorService.atualizar(id, dto)
        );
    }

    // --------------------------------------------------
    // MÉTODOS AUXILIARES
    // --------------------------------------------------

    private Usuario criarUsuario(Long id) {

        Usuario usuario = new Usuario();

        usuario.setId(id);
        usuario.setNome("Gabriel");
        usuario.setTelefone("83999999999");
        usuario.setEmail("gabriel@email.com");

        return usuario;
    }

    private DadosLeitor criarDadosLeitor(Usuario usuario) {

        return new DadosLeitor(
                usuario,
                LocalDate.of(2000, 5, 10),
                "Rua A",
                "123",
                "Casa",
                "Centro"
        );
    }
}