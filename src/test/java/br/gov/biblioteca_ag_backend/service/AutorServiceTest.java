
package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.autor.AutorRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.autor.AutorResumoDTO;
import br.gov.biblioteca_ag_backend.dto.autor.CriarAutorDTO;
import br.gov.biblioteca_ag_backend.exceptions.EmptyRequestException;
import br.gov.biblioteca_ag_backend.exceptions.RequestValidationException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceAlreadyExistsException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceNotFoundException;
import br.gov.biblioteca_ag_backend.model.Autor;
import br.gov.biblioteca_ag_backend.model.Livro;
import br.gov.biblioteca_ag_backend.repository.AutorRepository;
import br.gov.biblioteca_ag_backend.repository.LivroRepository;

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
class AutorServiceTest {

    @Mock
    private AutorRepository autorRepository;

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private AutorService autorService;

    private Autor autor;

    @BeforeEach
    void setUp() {
        autor = new Autor();
        autor.setNome("Machado de Assis");
    }

    // CADASTRO

    @Test
    void deveCadastrarAutor() {
        CriarAutorDTO dto = new CriarAutorDTO("Machado de Assis");

        when(autorRepository.save(any(Autor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AutorRespostaDTO resposta = autorService.cadastrar(dto);

        assertNotNull(resposta);
        verify(autorRepository).save(any(Autor.class));
    }

    // ATUALIZAÇÃO

    @Test
    void deveAtualizarAutor() {
        CriarAutorDTO dto = new CriarAutorDTO("Machado de Assis Atualizado");

        when(autorRepository.findById(1L))
                .thenReturn(Optional.of(autor));

        AutorRespostaDTO resposta = autorService.atualizarCompleto(1L, dto);

        assertNotNull(resposta);
        assertEquals("Machado de Assis Atualizado", autor.getNome());

        verify(autorRepository).findById(1L);
        verify(autorRepository, never()).save(any(Autor.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarAutorInexistente() {
        CriarAutorDTO dto = new CriarAutorDTO("Novo nome");

        when(autorRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> autorService.atualizarCompleto(99L, dto));

        verify(autorRepository, never()).save(any(Autor.class));
    }

    // BUSCA

    @Test
    void deveBuscarAutorPorId() {
        when(autorRepository.findById(1L))
                .thenReturn(Optional.of(autor));

        AutorResumoDTO resposta = autorService.buscarPorId(1L);

        assertNotNull(resposta);
        verify(autorRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarAutorInexistente() {
        when(autorRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> autorService.buscarPorId(99L));
    }

    @Test
    void deveListarTodosOsAutores() {
        Autor segundoAutor = new Autor();
        segundoAutor.setNome("Clarice Lispector");

        when(autorRepository.findAll())
                .thenReturn(List.of(autor, segundoAutor));

        List<AutorRespostaDTO> resposta = autorService.listarTodos();

        assertNotNull(resposta);
        assertEquals(2, resposta.size());
        verify(autorRepository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremAutores() {
        when(autorRepository.findAll()).thenReturn(List.of());

        List<AutorRespostaDTO> resposta = autorService.listarTodos();

        assertNotNull(resposta);
        assertTrue(resposta.isEmpty());
    }

    // EXCLUSÃO

    @Test
    void deveExcluirAutorSemLivrosAssociados() {
        when(autorRepository.findById(1L))
                .thenReturn(Optional.of(autor));

        assertDoesNotThrow(() -> autorService.excluir(1L));

        verify(autorRepository).delete(autor);
    }

    @Test
    void deveImpedirExclusaoDeAutorComLivrosAssociados() {
        Livro livro = new Livro();
        livro.setId(10L);

        autor.getLivros().add(livro);

        when(autorRepository.findById(1L))
                .thenReturn(Optional.of(autor));

        assertThrows(RequestValidationException.class,
                () -> autorService.excluir(1L));

        verify(autorRepository, never()).delete(any(Autor.class));
    }

    @Test
    void deveLancarExcecaoAoExcluirAutorInexistente() {
        when(autorRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> autorService.excluir(99L));

        verify(autorRepository, never()).delete(any(Autor.class));
    }

    // ADICIONAR LIVROS

    @Test
    void deveAdicionarLivrosAoAutor() {
        Livro livro1 = new Livro();
        livro1.setId(10L);

        Livro livro2 = new Livro();
        livro2.setId(20L);

        List<Long> ids = List.of(10L, 20L);

        when(autorRepository.findById(1L))
                .thenReturn(Optional.of(autor));

        when(livroRepository.findAllById(ids))
                .thenReturn(List.of(livro1, livro2));

        AutorResumoDTO resposta = autorService.adicionarLivros(1L, ids);

        assertNotNull(resposta);

        assertEquals(2, autor.getLivros().size());
        assertTrue(autor.getLivros().containsAll(List.of(livro1, livro2)));

        assertTrue(livro1.getAutores().contains(autor));
        assertTrue(livro2.getAutores().contains(autor));
    }

    @Test
    void deveImpedirAdicionarListaVazia() {
        assertThrows(EmptyRequestException.class,
                () -> autorService.adicionarLivros(1L, List.of()));

        verify(autorRepository, never()).findById(anyLong());
    }

    @Test
    void deveImpedirAdicionarListaNula() {
        assertThrows(EmptyRequestException.class,
                () -> autorService.adicionarLivros(1L, null));
    }

    @Test
    void deveLancarExcecaoAoAdicionarLivrosInexistentes() {
        Livro livro = new Livro();
        livro.setId(10L);

        List<Long> ids = List.of(10L, 20L);

        when(autorRepository.findById(1L))
                .thenReturn(Optional.of(autor));

        when(livroRepository.findAllById(ids))
                .thenReturn(List.of(livro));

        assertThrows(ResourceNotFoundException.class,
                () -> autorService.adicionarLivros(1L, ids));

        assertTrue(autor.getLivros().isEmpty());
    }

    @Test
    void deveImpedirAdicionarLivroJaAssociado() {
        Livro livro = new Livro();
        livro.setId(10L);

        autor.getLivros().add(livro);

        when(autorRepository.findById(1L))
                .thenReturn(Optional.of(autor));

        when(livroRepository.findAllById(List.of(10L)))
                .thenReturn(List.of(livro));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> autorService.adicionarLivros(1L, List.of(10L)));

        assertEquals(1, autor.getLivros().size());
    }

    // REMOVER LIVROS

    @Test
    void deveRemoverLivrosDoAutor() {
        Livro livro = new Livro();
        livro.setId(10L);

        autor.getLivros().add(livro);
        livro.getAutores().add(autor);

        when(autorRepository.findById(1L))
                .thenReturn(Optional.of(autor));

        when(livroRepository.findAllById(List.of(10L)))
                .thenReturn(List.of(livro));

        AutorResumoDTO resposta =
                autorService.removerLivros(1L, List.of(10L));

        assertNotNull(resposta);
        assertFalse(autor.getLivros().contains(livro));
        assertFalse(livro.getAutores().contains(autor));
    }

    @Test
    void deveImpedirRemoverListaVazia() {
        assertThrows(EmptyRequestException.class,
                () -> autorService.removerLivros(1L, List.of()));
    }

    @Test
    void deveLancarExcecaoAoRemoverLivroInexistente() {
        List<Long> ids = List.of(10L);

        when(autorRepository.findById(1L))
                .thenReturn(Optional.of(autor));

        when(livroRepository.findAllById(ids))
                .thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> autorService.removerLivros(1L, ids));
    }

    @Test
    void deveImpedirRemoverLivroNaoAssociado() {
        Livro livro = new Livro();
        livro.setId(10L);

        when(autorRepository.findById(1L))
                .thenReturn(Optional.of(autor));

        when(livroRepository.findAllById(List.of(10L)))
                .thenReturn(List.of(livro));

        assertThrows(ResourceNotFoundException.class,
                () -> autorService.removerLivros(1L, List.of(10L)));

        assertTrue(autor.getLivros().isEmpty());
    }
}
