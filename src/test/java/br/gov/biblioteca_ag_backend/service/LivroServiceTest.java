
package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.livro.AtualizarLivroDTO;
import br.gov.biblioteca_ag_backend.dto.livro.CriarLivroDTO;
import br.gov.biblioteca_ag_backend.dto.livro.LivroRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.livro.LivroResumoDTO;

import br.gov.biblioteca_ag_backend.exceptions.EmptyRequestException;
import br.gov.biblioteca_ag_backend.exceptions.RequestValidationException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceNotFoundException;

import br.gov.biblioteca_ag_backend.model.Autor;
import br.gov.biblioteca_ag_backend.model.Categoria;
import br.gov.biblioteca_ag_backend.model.Livro;

import br.gov.biblioteca_ag_backend.repository.AutorRepository;
import br.gov.biblioteca_ag_backend.repository.CategoriaRepository;
import br.gov.biblioteca_ag_backend.repository.LivroRepository;
import br.gov.biblioteca_ag_backend.repository.ExemplarRepository;
import br.gov.biblioteca_ag_backend.repository.ReservaRepository;
import br.gov.biblioteca_ag_backend.repository.EmprestimoRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private AutorRepository autorRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ExemplarRepository exemplarRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private CapaStorageService capaStorageService;

    @InjectMocks
    private LivroService livroService;

    private Livro livro;

    // CONFIGURAÇÃO

    @BeforeEach
    void setUp() {
        livro = new Livro();
        livro.setTitulo("Dom Casmurro");
        livro.setIsbn("123456789");
        livro.setEditora("Editora Exemplo");
        livro.setSinopse("Uma história de Machado de Assis.");
        livro.setAnoPublicacao(1899);

        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    // AUXILIARES

    private CriarLivroDTO novoDtoCadastro() {
        return new CriarLivroDTO(
                null,
                null,
                "Dom Casmurro",
                "123456789",
                "Editora Exemplo",
                "Uma história de Machado de Assis.",
                1899
        );
    }

    private CriarLivroDTO dtoCadastro(
            Long idCategoria,
            Set<Long> idsAutores
    ) {
        return new CriarLivroDTO(
                idCategoria,
                idsAutores,
                "Dom Casmurro",
                "123456789",
                "Editora Exemplo",
                "Uma história de Machado de Assis.",
                1899
        );
    }

    private AtualizarLivroDTO novoDtoAtualizacao() {
        return new AtualizarLivroDTO(
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private AtualizarLivroDTO dtoAtualizacao(
            Long idCategoria,
            Set<Long> idsAutores
    ) {
        return new AtualizarLivroDTO(
                null,
                null,
                null,
                null,
                null,
                idCategoria,
                idsAutores
        );
    }

    private void simularLivroEncontrado() {
        when(livroRepository.findById(1L))
                .thenReturn(Optional.of(livro));
    }

    private void simularLivroSemDependencias() {
        when(exemplarRepository.existsByLivro_Id(1L))
                .thenReturn(false);

        when(reservaRepository.existsByLivro_Id(1L))
                .thenReturn(false);

        when(emprestimoRepository.existsByExemplar_Livro_Id(1L))
                .thenReturn(false);
    }

    private void simularSalvamentoDaCapa(MultipartFile arquivo) {
        when(capaStorageService.salvar(arquivo))
                .thenReturn("capas/livro.webp");
    }

    // CADASTRO

    @Test
    void deveCadastrarLivro() {
        CriarLivroDTO dto = novoDtoCadastro();

        MultipartFile arquivo = mock(MultipartFile.class);
        simularSalvamentoDaCapa(arquivo);

        when(livroRepository.save(any(Livro.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LivroRespostaDTO resposta = livroService.cadastrar(dto, arquivo);

        assertNotNull(resposta);

        verify(capaStorageService).salvar(arquivo);
        verify(livroRepository).save(any(Livro.class));
    }

    @Test
    void deveAssociarCategoriaAoCadastrarLivro() {
        Categoria categoria = new Categoria();
        categoria.setNome("Literatura");

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        MultipartFile arquivo = mock(MultipartFile.class);
        simularSalvamentoDaCapa(arquivo);

        when(livroRepository.save(any(Livro.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CriarLivroDTO dto = dtoCadastro(1L, null);

        livroService.cadastrar(dto, arquivo);

        verify(categoriaRepository).findById(1L);

        verify(livroRepository).save(argThat(l ->
                l.getCategoria() == categoria
        ));
    }

    @Test
    void deveLancarExcecaoAoCadastrarComCategoriaInexistente() {
        CriarLivroDTO dto = dtoCadastro(99L, null);

        when(categoriaRepository.findById(99L))
                .thenReturn(Optional.empty());

        MultipartFile arquivo = mock(MultipartFile.class);

        assertThrows(ResourceNotFoundException.class,
                () -> livroService.cadastrar(dto, arquivo));

        verify(capaStorageService, never()).salvar(any());
        verify(livroRepository, never()).save(any());
    }

    @Test
    void deveAssociarAutoresAoCadastrarLivro() {
        CriarLivroDTO dto = dtoCadastro(null, Set.of(10L));

        Autor autor = new Autor();
        autor.setNome("Machado de Assis");

        when(autorRepository.findAllById(Set.of(10L)))
                .thenReturn(List.of(autor));

        MultipartFile arquivo = mock(MultipartFile.class);
        simularSalvamentoDaCapa(arquivo);

        when(livroRepository.save(any(Livro.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        livroService.cadastrar(dto, arquivo);

        verify(livroRepository).save(argThat(l ->
                l.getAutores().contains(autor)
        ));

        assertTrue(autor.getLivros().stream()
                .anyMatch(l -> l.getTitulo().equals("Dom Casmurro")));
    }

    @Test
    void deveLancarExcecaoAoCadastrarComAutorInexistente() {
        CriarLivroDTO dto = dtoCadastro(null, Set.of(10L, 20L));

        Autor autor = new Autor();
        autor.setId(10L);

        when(autorRepository.findAllById(Set.of(10L, 20L)))
                .thenReturn(List.of(autor));

        MultipartFile arquivo = mock(MultipartFile.class);

        assertThrows(ResourceNotFoundException.class,
                () -> livroService.cadastrar(dto, arquivo));

        verify(capaStorageService, never()).salvar(any());
        verify(livroRepository, never()).save(any());
    }

    // CONSULTAS

    @Test
    void deveListarTodosOsLivros() {
        when(livroRepository.findAll())
                .thenReturn(List.of(livro));

        List<LivroRespostaDTO> resposta = livroService.listarTodos();

        assertNotNull(resposta);
        assertEquals(1, resposta.size());

        verify(livroRepository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremLivros() {
        when(livroRepository.findAll())
                .thenReturn(List.of());

        List<LivroRespostaDTO> resposta = livroService.listarTodos();

        assertNotNull(resposta);
        assertTrue(resposta.isEmpty());
    }

    @Test
    void deveBuscarLivroPorId() {
        simularLivroEncontrado();

        LivroResumoDTO resposta = livroService.buscarPorId(1L);

        assertNotNull(resposta);

        verify(livroRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarLivroInexistente() {
        when(livroRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> livroService.buscarPorId(99L));
    }

    // PATCH

    @Test
    void deveAtualizarLivroParcialmente() {
        simularLivroEncontrado();

        AtualizarLivroDTO dto = new AtualizarLivroDTO(
                "Dom Casmurro Atualizado",
                null,
                null,
                null,
                null,
                null,
                null
        );

        LivroRespostaDTO resposta =
                livroService.atualizar(1L, dto, null);

        assertNotNull(resposta);
        assertEquals("Dom Casmurro Atualizado", livro.getTitulo());

        verify(livroRepository, never()).save(any());
    }

    @Test
    void deveManterCategoriaQuandoIdCategoriaForNuloNoPatch() {
        Categoria categoria = new Categoria();
        categoria.setNome("Literatura");

        livro.setCategoria(categoria);

        simularLivroEncontrado();

        livroService.atualizar(1L, novoDtoAtualizacao(), null);

        assertSame(categoria, livro.getCategoria());

        verify(categoriaRepository, never()).findById(any());
    }

    @Test
    void deveAtualizarCategoriaNoPatch() {
        simularLivroEncontrado();

        Categoria novaCategoria = new Categoria();
        novaCategoria.setNome("Ficção");

        when(categoriaRepository.findById(2L))
                .thenReturn(Optional.of(novaCategoria));

        livroService.atualizar(
                1L,
                dtoAtualizacao(2L, null),
                null
        );

        assertSame(novaCategoria, livro.getCategoria());
    }

    @Test
    void deveManterAutoresQuandoListaForNulaNoPatch() {
        Autor autor = new Autor();
        autor.setNome("Machado de Assis");

        livro.getAutores().add(autor);
        autor.getLivros().add(livro);

        simularLivroEncontrado();

        livroService.atualizar(1L, novoDtoAtualizacao(), null);

        assertTrue(livro.getAutores().contains(autor));
        assertTrue(autor.getLivros().contains(livro));
    }

    @Test
    void deveRemoverTodosOsAutoresQuandoListaEstiverVaziaNoPatch() {
        Autor autor = new Autor();
        autor.setNome("Machado de Assis");

        livro.getAutores().add(autor);
        autor.getLivros().add(livro);

        simularLivroEncontrado();

        livroService.atualizar(
                1L,
                dtoAtualizacao(null, Set.of()),
                null
        );

        assertTrue(livro.getAutores().isEmpty());
        assertFalse(autor.getLivros().contains(livro));
    }

    @Test
    void deveLancarExcecaoAoAtualizarComCategoriaInexistente() {
        simularLivroEncontrado();

        when(categoriaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> livroService.atualizar(
                        1L,
                        dtoAtualizacao(99L, null),
                        null
                ));
    }

    // PUT

    @Test
    void deveImpedirPutSemNovaCapa() {
        simularLivroEncontrado();

        assertThrows(EmptyRequestException.class,
                () -> livroService.atualizarCompleto(
                        1L,
                        novoDtoCadastro(),
                        null
                ));

        verify(capaStorageService, never()).salvar(any());
    }

    @Test
    void deveAtualizarLivroComNovaCapaNoPut() {
        simularLivroEncontrado();

        MultipartFile novaCapa = mock(MultipartFile.class);

        livro.setCapa("capas/antiga.webp");

        when(capaStorageService.salvar(novaCapa))
                .thenReturn("capas/nova.webp");

        LivroRespostaDTO resposta = livroService.atualizarCompleto(
                1L,
                dtoCadastro(null, Set.of()),
                novaCapa
        );

        assertNotNull(resposta);
        assertEquals("capas/nova.webp", livro.getCapa());

        verify(capaStorageService).salvar(novaCapa);
    }

    @Test
    void deveRemoverCategoriaQuandoIdCategoriaForNuloNoPut() {
        simularLivroEncontrado();

        Categoria categoria = new Categoria();
        livro.setCategoria(categoria);

        MultipartFile novaCapa = mock(MultipartFile.class);
        simularSalvamentoDaCapa(novaCapa);

        livroService.atualizarCompleto(
                1L,
                novoDtoCadastro(),
                novaCapa
        );

        assertNull(livro.getCategoria());
    }

    @Test
    void deveRemoverAutoresQuandoListaForNulaNoPut() {
        simularLivroEncontrado();

        Autor autor = new Autor();
        autor.setNome("Machado de Assis");

        livro.getAutores().add(autor);
        autor.getLivros().add(livro);

        MultipartFile novaCapa = mock(MultipartFile.class);
        simularSalvamentoDaCapa(novaCapa);

        livroService.atualizarCompleto(
                1L,
                novoDtoCadastro(),
                novaCapa
        );

        assertTrue(livro.getAutores().isEmpty());
        assertFalse(autor.getLivros().contains(livro));
    }

    // CAPAS E TRANSAÇÕES

    @Test
    void deveExcluirCapaAntigaAposCommit() {
        simularLivroEncontrado();

        livro.setCapa("capas/antiga.webp");

        MultipartFile novaCapa = mock(MultipartFile.class);

        when(capaStorageService.salvar(novaCapa))
                .thenReturn("capas/nova.webp");

        livroService.atualizar(
                1L,
                novoDtoAtualizacao(),
                novaCapa
        );

        List<TransactionSynchronization> sincronizacoes =
                TransactionSynchronizationManager.getSynchronizations();

        sincronizacoes.forEach(TransactionSynchronization::afterCommit);

        verify(capaStorageService).excluir("capas/antiga.webp");
    }

    @Test
    void deveExcluirCapaNovaAposRollback() {
        simularLivroEncontrado();

        MultipartFile novaCapa = mock(MultipartFile.class);

        when(capaStorageService.salvar(novaCapa))
                .thenReturn("capas/nova.webp");

        livroService.atualizar(
                1L,
                novoDtoAtualizacao(),
                novaCapa
        );

        List<TransactionSynchronization> sincronizacoes =
                TransactionSynchronizationManager.getSynchronizations();

        sincronizacoes.forEach(sync ->
                sync.afterCompletion(
                        TransactionSynchronization.STATUS_ROLLED_BACK
                )
        );

        verify(capaStorageService).excluir("capas/nova.webp");
    }

    // EXCLUSÃO

    @Test
    void deveExcluirLivroSemDependencias() {
        simularLivroEncontrado();
        simularLivroSemDependencias();

        livro.setCapa("capas/livro.webp");

        assertDoesNotThrow(() -> livroService.deletar(1L));

        verify(livroRepository).delete(livro);
    }

    @Test
    void deveBloquearExclusaoQuandoExistiremExemplares() {
        simularLivroEncontrado();

        when(exemplarRepository.existsByLivro_Id(1L))
                .thenReturn(true);

        when(reservaRepository.existsByLivro_Id(1L))
                .thenReturn(false);

        when(emprestimoRepository.existsByExemplar_Livro_Id(1L))
                .thenReturn(false);

        assertThrows(RequestValidationException.class,
                () -> livroService.deletar(1L));

        verify(livroRepository, never()).delete(any(Livro.class));
    }

    @Test
    void deveBloquearExclusaoQuandoExistiremReservas() {
        simularLivroEncontrado();

        when(exemplarRepository.existsByLivro_Id(1L))
                .thenReturn(false);

        when(reservaRepository.existsByLivro_Id(1L))
                .thenReturn(true);

        when(emprestimoRepository.existsByExemplar_Livro_Id(1L))
                .thenReturn(false);

        assertThrows(RequestValidationException.class,
                () -> livroService.deletar(1L));

        verify(livroRepository, never()).delete(any(Livro.class));
    }

    @Test
    void deveBloquearExclusaoQuandoExistiremEmprestimos() {
        simularLivroEncontrado();

        when(exemplarRepository.existsByLivro_Id(1L))
                .thenReturn(false);

        when(reservaRepository.existsByLivro_Id(1L))
                .thenReturn(false);

        when(emprestimoRepository.existsByExemplar_Livro_Id(1L))
                .thenReturn(true);

        assertThrows(RequestValidationException.class,
                () -> livroService.deletar(1L));

        verify(livroRepository, never()).delete(any(Livro.class));
    }

    @Test
    void deveLancarExcecaoAoExcluirLivroInexistente() {
        when(livroRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> livroService.deletar(99L));

        verify(livroRepository, never()).delete(any(Livro.class));
    }
}
