
package br.gov.biblioteca_ag_backend.controller;

import br.gov.biblioteca_ag_backend.dto.exemplar.*;
import br.gov.biblioteca_ag_backend.enums.StatusExemplar;
import br.gov.biblioteca_ag_backend.service.ExemplarService;
import br.gov.biblioteca_ag_backend.validation.RequestValidator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExemplarController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExemplarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExemplarService exemplarService;

    @MockitoBean
    private RequestValidator requestValidator;

    private ExemplarRespostaDTO criarExemplarResposta() {
        return new ExemplarRespostaDTO(
                1L,
                10L,
                "Dom Casmurro",
                "Estante A1",
                "TOMB-001",
                StatusExemplar.DISPONIVEL
        );
    }

    private ExemplarResumoDTO criarExemplarResumo() {
        return new ExemplarResumoDTO(
                1L,
                10L,
                "Dom Casmurro",
                "Estante A1",
                "TOMB-001",
                StatusExemplar.DISPONIVEL
        );
    }

    @Test
    void listarTodos_DeveRetornarStatus200() throws Exception {

        when(exemplarService.listarTodos())
                .thenReturn(List.of(criarExemplarResposta()));

        mockMvc.perform(get("/exemplares"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].id_livro").value(10))
                .andExpect(jsonPath("$[0].titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$[0].localizaoEstante").value("Estante A1"))
                .andExpect(jsonPath("$[0].nTombamento").value("TOMB-001"))
                .andExpect(jsonPath("$[0].status").value("DISPONIVEL"));

        verify(exemplarService).listarTodos();
    }

    @Test
    void listarTodos_DeveRetornarListaVazia() throws Exception {

        when(exemplarService.listarTodos())
                .thenReturn(List.of());

        mockMvc.perform(get("/exemplares"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(exemplarService).listarTodos();
    }

    @Test
    void buscarPorId_DeveRetornarStatus200() throws Exception {

        when(exemplarService.buscarPorId(1L))
                .thenReturn(criarExemplarResumo());

        mockMvc.perform(get("/exemplares/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.id_livro").value(10))
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.localizaoEstante").value("Estante A1"))
                .andExpect(jsonPath("$.nTombamento").value("TOMB-001"))
                .andExpect(jsonPath("$.statusExemplar").value("DISPONIVEL"));

        verify(exemplarService).buscarPorId(1L);
    }

    @Test
    void buscarPorLivroId_DeveRetornarStatus200() throws Exception {

        when(exemplarService.buscarPorLivroId(10L))
                .thenReturn(List.of(criarExemplarResposta()));

        mockMvc.perform(get("/exemplares/livro/{id}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].id_livro").value(10))
                .andExpect(jsonPath("$[0].titulo").value("Dom Casmurro"));

        verify(exemplarService).buscarPorLivroId(10L);
    }

    @Test
    void cadastrar_DeveRetornarStatus201() throws Exception {

        when(exemplarService.cadastrar(any(CriarExemplarDTO.class)))
                .thenReturn(criarExemplarResposta());

        mockMvc.perform(post("/exemplares")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id_livro": 10,
                                    "localizaoEstante": "Estante A1",
                                    "nTombamento": "TOMB-001"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.id_livro").value(10))
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.nTombamento").value("TOMB-001"))
                .andExpect(jsonPath("$.status").value("DISPONIVEL"));

        verify(requestValidator).validar(any(CriarExemplarDTO.class));
        verify(exemplarService).cadastrar(any(CriarExemplarDTO.class));
    }

    @Test
    void atualizar_DeveRetornarStatus200() throws Exception {

        when(exemplarService.atualizar(
                eq(1L),
                any(AtualizarExemplarDTO.class)
        )).thenReturn(criarExemplarResumo());

        mockMvc.perform(patch("/exemplares/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "localizaoEstante": "Estante B2"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.id_livro").value(10))
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.statusExemplar").value("DISPONIVEL"));

        verify(requestValidator).validar(any(AtualizarExemplarDTO.class));

        verify(exemplarService).atualizar(
                eq(1L),
                any(AtualizarExemplarDTO.class)
        );
    }

    @Test
    void deletar_DeveRetornarStatus204() throws Exception {

        mockMvc.perform(delete("/exemplares/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(exemplarService).excluir(1L);
    }
}
