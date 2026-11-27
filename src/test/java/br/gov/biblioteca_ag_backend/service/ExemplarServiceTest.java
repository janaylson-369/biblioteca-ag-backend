
package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.exemplar.AtualizarExemplarDTO;
import br.gov.biblioteca_ag_backend.dto.exemplar.CriarExemplarDTO;
import br.gov.biblioteca_ag_backend.dto.exemplar.ExemplarRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.exemplar.ExemplarResumoDTO;
import br.gov.biblioteca_ag_backend.enums.StatusExemplar;
import br.gov.biblioteca_ag_backend.exceptions.RequestValidationException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceAlreadyExistsException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceNotFoundException;
import br.gov.biblioteca_ag_backend.model.Exemplar;
import br.gov.biblioteca_ag_backend.model.Livro;
import br.gov.biblioteca_ag_backend.repository.EmprestimoRepository;
import br.gov.biblioteca_ag_backend.repository.ExemplarRepository;
import br.gov.biblioteca_ag_backend.repository.LivroRepository;
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
class ExemplarServiceTest {

    @Mock
    private ExemplarRepository exemplarRepository;

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @InjectMocks
    private ExemplarService exemplarService;

    // --------------------------------------------------
    // CADASTRAR
    // --------------------------------------------------

    @Test
    void cadastrarComSucesso() {

        Long idLivro = 1L;

        Livro livro = criarLivro(idLivro);

        CriarExemplarDTO dto = new CriarExemplarDTO(
                idLivro,
                "Estante A",
                "TOMB-001"
        );

        when(livroRepository.findById(idLivro))
                .thenReturn(Optional.of(livro));

        when(exemplarRepository.existsByNTombamento("TOMB-001"))
                .thenReturn(false);

        when(exemplarRepository.save(any(Exemplar.class)))
                .thenAnswer(invocation -> {
                    Exemplar ex = invocation.getArgument(0);
                    ex.setId(10L);
                    return ex;
                });

        ExemplarRespostaDTO resposta = exemplarService.cadastrar(dto);

        assertNotNull(resposta);
        assertEquals(10L, resposta.id());
        assertEquals(idLivro, resposta.id_livro());
        assertEquals("Livro de teste", resposta.titulo());
        assertEquals("TOMB-001", resposta.nTombamento());
        assertEquals(StatusExemplar.DISPONIVEL, resposta.status());

        verify(exemplarRepository).save(any(Exemplar.class));
    }

    @Test
    void cadastrarQuandoLivroNaoExiste() {

        Long idLivro = 1L;

        CriarExemplarDTO dto = new CriarExemplarDTO(
                idLivro,
                "Estante A",
                "TOMB-001"
        );

        when(livroRepository.findById(idLivro))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> exemplarService.cadastrar(dto)
        );

        verify(exemplarRepository, never())
                .save(any(Exemplar.class));
    }

    @Test
    void cadastrarComTombamentoDuplicado() {

        Long idLivro = 1L;

        Livro livro = criarLivro(idLivro);

        CriarExemplarDTO dto = new CriarExemplarDTO(
                idLivro,
                "Estante A",
                "TOMB-001"
        );

        when(livroRepository.findById(idLivro))
                .thenReturn(Optional.of(livro));

        when(exemplarRepository.existsByNTombamento("TOMB-001"))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> exemplarService.cadastrar(dto)
        );

        verify(exemplarRepository, never())
                .save(any(Exemplar.class));
    }

    // --------------------------------------------------
    // LISTAR
    // --------------------------------------------------

    @Test
    void listarTodosComSucesso() {

        Livro livro = criarLivro(1L);

        Exemplar ex1 = criarExemplar(1L, livro, "TOMB-001");
        Exemplar ex2 = criarExemplar(2L, livro, "TOMB-002");

        when(exemplarRepository.findAll())
                .thenReturn(List.of(ex1, ex2));

        List<ExemplarRespostaDTO> resposta =
                exemplarService.listarTodos();

        assertNotNull(resposta);
        assertEquals(2, resposta.size());

        assertEquals("TOMB-001", resposta.get(0).nTombamento());
        assertEquals("TOMB-002", resposta.get(1).nTombamento());

        verify(exemplarRepository).findAll();
    }

    @Test
    void listarTodosSemExemplares() {

        when(exemplarRepository.findAll())
                .thenReturn(List.of());

        List<ExemplarRespostaDTO> resposta =
                exemplarService.listarTodos();

        assertNotNull(resposta);
        assertTrue(resposta.isEmpty());
    }

    // --------------------------------------------------
    // BUSCAR POR ID
    // --------------------------------------------------

    @Test
    void buscarPorIdComSucesso() {

        Long id = 1L;

        Livro livro = criarLivro(10L);
        Exemplar exemplar = criarExemplar(id, livro, "TOMB-001");

        when(exemplarRepository.findById(id))
                .thenReturn(Optional.of(exemplar));

        ExemplarResumoDTO resposta =
                exemplarService.buscarPorId(id);

        assertNotNull(resposta);
        assertEquals(id, resposta.id());
        assertEquals(10L, resposta.id_livro());
        assertEquals("Livro de teste", resposta.titulo());
        assertEquals("TOMB-001", resposta.nTombamento());
        assertEquals(StatusExemplar.DISPONIVEL, resposta.statusExemplar());
    }

    @Test
    void buscarPorIdQuandoExemplarNaoExiste() {

        Long id = 1L;

        when(exemplarRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> exemplarService.buscarPorId(id)
        );
    }

    // --------------------------------------------------
    // BUSCAR POR LIVRO
    // --------------------------------------------------

    @Test
    void buscarPorLivroIdComSucesso() {

        Long idLivro = 1L;

        Livro livro = criarLivro(idLivro);

        Exemplar ex1 = criarExemplar(1L, livro, "TOMB-001");
        Exemplar ex2 = criarExemplar(2L, livro, "TOMB-002");

        when(livroRepository.existsById(idLivro))
                .thenReturn(true);

        when(exemplarRepository.findByLivro_Id(idLivro))
                .thenReturn(List.of(ex1, ex2));

        List<ExemplarRespostaDTO> resposta =
                exemplarService.buscarPorLivroId(idLivro);

        assertNotNull(resposta);
        assertEquals(2, resposta.size());
        assertEquals("TOMB-001", resposta.get(0).nTombamento());
        assertEquals("TOMB-002", resposta.get(1).nTombamento());

        verify(livroRepository).existsById(idLivro);
        verify(exemplarRepository).findByLivro_Id(idLivro);
    }

    @Test
    void buscarPorLivroIdQuandoLivroNaoExiste() {

        Long idLivro = 1L;

        when(livroRepository.existsById(idLivro))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> exemplarService.buscarPorLivroId(idLivro)
        );

        verify(exemplarRepository, never())
                .findByLivro_Id(anyLong());
    }

    @Test
    void buscarPorLivroIdSemExemplares() {

        Long idLivro = 1L;

        when(livroRepository.existsById(idLivro))
                .thenReturn(true);

        when(exemplarRepository.findByLivro_Id(idLivro))
                .thenReturn(List.of());

        List<ExemplarRespostaDTO> resposta =
                exemplarService.buscarPorLivroId(idLivro);

        assertNotNull(resposta);
        assertTrue(resposta.isEmpty());
    }

    // --------------------------------------------------
    // ATUALIZAR
    // --------------------------------------------------

    @Test
    void atualizarComSucesso() {

        Long id = 1L;

        Livro livro = criarLivro(10L);
        Exemplar exemplar = criarExemplar(id, livro, "TOMB-001");

        AtualizarExemplarDTO dto = new AtualizarExemplarDTO(
                "Estante B",
                "TOMB-002"
        );

        when(exemplarRepository.findById(id))
                .thenReturn(Optional.of(exemplar));

        when(exemplarRepository.existsByNTombamentoAndIdNot(
                "TOMB-002", id
        )).thenReturn(false);

        ExemplarResumoDTO resposta =
                exemplarService.atualizar(id, dto);

        assertNotNull(resposta);
        assertEquals("Estante B", exemplar.getLocalizacaoEstante());
        assertEquals("TOMB-002", exemplar.getNTombamento());

        verify(exemplarRepository, never())
                .save(any(Exemplar.class));
    }

    @Test
    void atualizarParcialmente() {

        Long id = 1L;

        Livro livro = criarLivro(10L);
        Exemplar exemplar = criarExemplar(id, livro, "TOMB-001");

        AtualizarExemplarDTO dto = new AtualizarExemplarDTO(
                "Estante C",
                null
        );

        when(exemplarRepository.findById(id))
                .thenReturn(Optional.of(exemplar));

        ExemplarResumoDTO resposta =
                exemplarService.atualizar(id, dto);

        assertNotNull(resposta);
        assertEquals("Estante C", exemplar.getLocalizacaoEstante());

        // Tombamento não enviado deve permanecer igual.
        assertEquals("TOMB-001", exemplar.getNTombamento());

        verify(exemplarRepository, never())
                .existsByNTombamentoAndIdNot(anyString(), anyLong());
    }

    @Test
    void atualizarMantendoMesmoTombamento() {

        Long id = 1L;

        Livro livro = criarLivro(10L);
        Exemplar exemplar = criarExemplar(id, livro, "TOMB-001");

        AtualizarExemplarDTO dto = new AtualizarExemplarDTO(
                "Estante B",
                "TOMB-001"
        );

        when(exemplarRepository.findById(id))
                .thenReturn(Optional.of(exemplar));

        exemplarService.atualizar(id, dto);

        assertEquals("TOMB-001", exemplar.getNTombamento());

        verify(exemplarRepository, never())
                .existsByNTombamentoAndIdNot(anyString(), anyLong());
    }

    @Test
    void atualizarComTombamentoDuplicado() {

        Long id = 1L;

        Livro livro = criarLivro(10L);
        Exemplar exemplar = criarExemplar(id, livro, "TOMB-001");

        AtualizarExemplarDTO dto = new AtualizarExemplarDTO(
                "Estante B",
                "TOMB-002"
        );

        when(exemplarRepository.findById(id))
                .thenReturn(Optional.of(exemplar));

        when(exemplarRepository.existsByNTombamentoAndIdNot(
                "TOMB-002", id
        )).thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> exemplarService.atualizar(id, dto)
        );

        assertEquals("TOMB-001", exemplar.getNTombamento());
    }

    @Test
    void atualizarQuandoExemplarNaoExiste() {

        Long id = 1L;

        AtualizarExemplarDTO dto = new AtualizarExemplarDTO(
                "Estante B",
                null
        );

        when(exemplarRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> exemplarService.atualizar(id, dto)
        );
    }

    // --------------------------------------------------
    // EXCLUIR
    // --------------------------------------------------

    @Test
    void excluirComSucesso() {

        Long id = 1L;

        Livro livro = criarLivro(10L);
        Exemplar exemplar = criarExemplar(id, livro, "TOMB-001");

        when(exemplarRepository.findById(id))
                .thenReturn(Optional.of(exemplar));

        when(emprestimoRepository.existsByExemplar_Id(id))
                .thenReturn(false);

        exemplarService.excluir(id);

        verify(exemplarRepository).delete(exemplar);
    }

    @Test
    void excluirQuandoExistemEmprestimos() {

        Long id = 1L;

        Livro livro = criarLivro(10L);
        Exemplar exemplar = criarExemplar(id, livro, "TOMB-001");

        when(exemplarRepository.findById(id))
                .thenReturn(Optional.of(exemplar));

        when(emprestimoRepository.existsByExemplar_Id(id))
                .thenReturn(true);

        assertThrows(
                RequestValidationException.class,
                () -> exemplarService.excluir(id)
        );

        verify(exemplarRepository, never()).delete(any(Exemplar.class));
    }

    @Test
    void excluirQuandoExemplarNaoExiste() {

        Long id = 1L;

        when(exemplarRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> exemplarService.excluir(id)
        );

        verify(emprestimoRepository, never())
                .existsByExemplar_Id(anyLong());
    }

    // --------------------------------------------------
    // MÉTODOS AUXILIARES
    // --------------------------------------------------

    private Livro criarLivro(Long id) {

        Livro livro = new Livro();

        livro.setId(id);
        livro.setTitulo("Livro de teste");

        return livro;
    }

    private Exemplar criarExemplar(
            Long id,
            Livro livro,
            String tombamento
    ) {

        Exemplar exemplar = new Exemplar(
                livro,
                "Estante A",
                tombamento
        );

        exemplar.setId(id);
        exemplar.setStatus(StatusExemplar.DISPONIVEL);

        return exemplar;
    }
}

