
package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.categoria.CategoriaRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.categoria.CategoriaResumoDTO;
import br.gov.biblioteca_ag_backend.dto.categoria.CriarCategoriaDTO;
import br.gov.biblioteca_ag_backend.exceptions.ResourceNotFoundException;
import br.gov.biblioteca_ag_backend.model.Categoria;
import br.gov.biblioteca_ag_backend.model.Livro;
import br.gov.biblioteca_ag_backend.repository.CategoriaRepository;
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
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setNome("Literatura");
    }

    // =========================
    // CADASTRAR
    // =========================

    @Test
    void deveCadastrarCategoria() {
        CriarCategoriaDTO dto = new CriarCategoriaDTO("Literatura");

        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoriaRespostaDTO resposta = categoriaService.cadastrar(dto);

        assertNotNull(resposta);

        verify(categoriaRepository).save(any(Categoria.class));
    }

    // =========================
    // BUSCAR POR ID
    // =========================

    @Test
    void deveBuscarCategoriaPorId() {
        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        CategoriaResumoDTO resposta = categoriaService.buscarPorId(1L);

        assertNotNull(resposta);

        verify(categoriaRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoExistirNaBusca() {
        when(categoriaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoriaService.buscarPorId(99L)
        );

        verify(categoriaRepository).findById(99L);
    }

    // =========================
    // LISTAR TODOS
    // =========================

    @Test
    void deveListarTodasAsCategorias() {
        Categoria segundaCategoria = new Categoria();
        segundaCategoria.setNome("Ciências");

        when(categoriaRepository.findAll())
                .thenReturn(List.of(categoria, segundaCategoria));

        List<CategoriaRespostaDTO> resposta =
                categoriaService.listarTodos();

        assertNotNull(resposta);
        assertEquals(2, resposta.size());

        verify(categoriaRepository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremCategorias() {
        when(categoriaRepository.findAll())
                .thenReturn(List.of());

        List<CategoriaRespostaDTO> resposta =
                categoriaService.listarTodos();

        assertNotNull(resposta);
        assertTrue(resposta.isEmpty());

        verify(categoriaRepository).findAll();
    }

    // =========================
    // ATUALIZAR
    // =========================

    @Test
    void deveAtualizarCategoria() {
        CriarCategoriaDTO dto = new CriarCategoriaDTO("Ficção");

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        CategoriaRespostaDTO resposta =
                categoriaService.atualizar(1L, dto);

        assertNotNull(resposta);
        assertEquals("Ficção", categoria.getNome());

        verify(categoriaRepository).findById(1L);

        // O save não é necessário devido ao Dirty Checking.
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarCategoriaInexistente() {
        CriarCategoriaDTO dto = new CriarCategoriaDTO("Ficção");

        when(categoriaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoriaService.atualizar(99L, dto)
        );

        verify(categoriaRepository).findById(99L);
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    // =========================
    // EXCLUIR
    // =========================

    @Test
    void deveExcluirCategoriaSemLivrosAssociados() {
        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        when(livroRepository.findByCategoria_Id(1L))
                .thenReturn(List.of());

        assertDoesNotThrow(() -> categoriaService.excluir(1L));

        verify(livroRepository).findByCategoria_Id(1L);
        verify(categoriaRepository).delete(categoria);
    }

    @Test
    void deveExcluirCategoriaDesvinculandoOsLivros() {
        Livro livro1 = new Livro();
        Livro livro2 = new Livro();

        livro1.setCategoria(categoria);
        livro2.setCategoria(categoria);

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        when(livroRepository.findByCategoria_Id(1L))
                .thenReturn(List.of(livro1, livro2));

        assertDoesNotThrow(() -> categoriaService.excluir(1L));

        assertNull(livro1.getCategoria());
        assertNull(livro2.getCategoria());

        verify(livroRepository).findByCategoria_Id(1L);
        verify(categoriaRepository).delete(categoria);
    }

    @Test
    void deveLancarExcecaoAoExcluirCategoriaInexistente() {
        when(categoriaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoriaService.excluir(99L)
        );

        verify(categoriaRepository, never()).delete(any(Categoria.class));
        verify(livroRepository, never()).findByCategoria_Id(anyLong());
    }
}
