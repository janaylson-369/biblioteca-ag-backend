
package br.gov.biblioteca_ag_backend.controller;

import br.gov.biblioteca_ag_backend.dto.autor.AutorRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.autor.AutorResumoDTO;
import br.gov.biblioteca_ag_backend.dto.autor.CriarAutorDTO;
import br.gov.biblioteca_ag_backend.service.AutorService;
import br.gov.biblioteca_ag_backend.validation.RequestValidator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AutorController.class)
@AutoConfigureMockMvc(addFilters = false)
class AutorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutorService autorService;

    @MockitoBean
    private RequestValidator requestValidator;

    // -------------------------------------------------------
    // OBJETOS AUXILIARES
    // -------------------------------------------------------

    private AutorRespostaDTO criarAutorResposta() {
        return new AutorRespostaDTO(
                1L,
                "Machado de Assis"
        );
    }

    private AutorResumoDTO criarAutorResumo() {
        return new AutorResumoDTO(
                1L,
                "Machado de Assis",
                Set.of()
        );
    }

    // -------------------------------------------------------
    // GET /autores
    // -------------------------------------------------------

    @Test
    void listarTodos_DeveRetornarStatus200() throws Exception {

        when(autorService.listarTodos())
                .thenReturn(List.of(criarAutorResposta()));

        mockMvc.perform(get("/autores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Machado de Assis"));

        verify(autorService).listarTodos();
    }

    @Test
    void listarTodos_DeveRetornarListaVazia() throws Exception {

        when(autorService.listarTodos())
                .thenReturn(List.of());

        mockMvc.perform(get("/autores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(autorService).listarTodos();
    }

    // -------------------------------------------------------
    // GET /autores/{id}
    // -------------------------------------------------------

    @Test
    void procurarPorId_DeveRetornarStatus200() throws Exception {

        when(autorService.buscarPorId(1L))
                .thenReturn(criarAutorResumo());

        mockMvc.perform(get("/autores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Machado de Assis"))
                .andExpect(jsonPath("$.livros").isArray());

        verify(autorService).buscarPorId(1L);
    }

    // -------------------------------------------------------
    // POST /autores
    // -------------------------------------------------------

    @Test
    void cadastrar_DeveRetornarStatus201() throws Exception {

        when(autorService.cadastrar(any(CriarAutorDTO.class)))
                .thenReturn(criarAutorResposta());

        mockMvc.perform(post("/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "nome": "Machado de Assis"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Machado de Assis"));

        verify(autorService).cadastrar(any(CriarAutorDTO.class));
    }

    // -------------------------------------------------------
    // PUT /autores/{id}
    // -------------------------------------------------------

    @Test
    void atualizarCompleto_DeveRetornarStatus200() throws Exception {

        when(autorService.atualizarCompleto(
                eq(1L),
                any(CriarAutorDTO.class)
        )).thenReturn(criarAutorResposta());

        mockMvc.perform(put("/autores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "nome": "Machado de Assis Atualizado"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Machado de Assis"));

        verify(autorService).atualizarCompleto(
                eq(1L),
                any(CriarAutorDTO.class)
        );
    }

    // -------------------------------------------------------
    // DELETE /autores/{id}
    // -------------------------------------------------------

    @Test
    void deletar_DeveRetornarStatus204() throws Exception {

        doNothing().when(autorService).excluir(1L);

        mockMvc.perform(delete("/autores/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(autorService).excluir(1L);
    }

    // -------------------------------------------------------
    // POST /autores/{id}/livros
    // -------------------------------------------------------

    @Test
    void adicionarLivros_DeveRetornarStatus200() throws Exception {

        when(autorService.adicionarLivros(1L, List.of(2L, 3L)))
                .thenReturn(criarAutorResumo());

        mockMvc.perform(post("/autores/1/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[2, 3]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Machado de Assis"));

        verify(autorService).adicionarLivros(1L, List.of(2L, 3L));
    }

    // -------------------------------------------------------
    // DELETE /autores/{id}/livros
    // -------------------------------------------------------

    @Test
    void removerLivros_DeveRetornarStatus200() throws Exception {

        when(autorService.removerLivros(1L, List.of(2L, 3L)))
                .thenReturn(criarAutorResumo());

        mockMvc.perform(delete("/autores/1/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[2, 3]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Machado de Assis"));

        verify(autorService).removerLivros(1L, List.of(2L, 3L));
    }
}
