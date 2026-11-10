
package br.gov.biblioteca_ag_backend.controller;

import br.gov.biblioteca_ag_backend.dto.categoria.CategoriaRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.categoria.CategoriaResumoDTO;
import br.gov.biblioteca_ag_backend.dto.categoria.CriarCategoriaDTO;
import br.gov.biblioteca_ag_backend.service.CategoriaService;
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

@WebMvcTest(CategoriaController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService categoriaService;

    @MockitoBean
    private RequestValidator requestValidator;

    // -------------------------------------------------------
    // OBJETOS AUXILIARES
    // -------------------------------------------------------

    private CategoriaRespostaDTO criarCategoriaResposta() {
        return new CategoriaRespostaDTO(
                1L,
                "Literatura"
        );
    }

    private CategoriaResumoDTO criarCategoriaResumo() {
        return new CategoriaResumoDTO(
                1L,
                "Literatura",
                List.of()
        );
    }

    // -------------------------------------------------------
    // GET /categorias
    // -------------------------------------------------------

    @Test
    void listarTodos_DeveRetornarStatus200() throws Exception {

        when(categoriaService.listarTodos())
                .thenReturn(List.of(criarCategoriaResposta()));

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Literatura"));

        verify(categoriaService).listarTodos();
    }

    @Test
    void listarTodos_DeveRetornarListaVazia() throws Exception {

        when(categoriaService.listarTodos())
                .thenReturn(List.of());

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(categoriaService).listarTodos();
    }

    // -------------------------------------------------------
    // GET /categorias/{id}
    // -------------------------------------------------------

    @Test
    void buscarPorId_DeveRetornarStatus200() throws Exception {

        when(categoriaService.buscarPorId(1L))
                .thenReturn(criarCategoriaResumo());

        mockMvc.perform(get("/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Literatura"))
                .andExpect(jsonPath("$.livros").isArray());

        verify(categoriaService).buscarPorId(1L);
    }

    // -------------------------------------------------------
    // POST /categorias
    // -------------------------------------------------------

    @Test
    void cadastrar_DeveRetornarStatus201() throws Exception {

        when(categoriaService.cadastrar(any(CriarCategoriaDTO.class)))
                .thenReturn(criarCategoriaResposta());

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "nome": "Literatura"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Literatura"));

        verify(requestValidator).validar(any(CriarCategoriaDTO.class));
        verify(categoriaService).cadastrar(any(CriarCategoriaDTO.class));
    }

    // -------------------------------------------------------
    // PUT /categorias/{id}
    // -------------------------------------------------------

    @Test
    void atualizar_DeveRetornarStatus200() throws Exception {

        when(categoriaService.atualizar(
                eq(1L),
                any(CriarCategoriaDTO.class)
        )).thenReturn(criarCategoriaResposta());

        mockMvc.perform(put("/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "nome": "Literatura Brasileira"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Literatura"));

        verify(requestValidator).validar(any(CriarCategoriaDTO.class));
        verify(categoriaService).atualizar(
                eq(1L),
                any(CriarCategoriaDTO.class)
        );
    }

    // -------------------------------------------------------
    // DELETE /categorias/{id}
    // -------------------------------------------------------

    @Test
    void deletar_DeveRetornarStatus204() throws Exception {

        doNothing().when(categoriaService).excluir(1L);

        mockMvc.perform(delete("/categorias/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(categoriaService).excluir(1L);
    }
}
