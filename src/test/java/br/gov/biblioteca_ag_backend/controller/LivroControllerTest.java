
package br.gov.biblioteca_ag_backend.controller;

import br.gov.biblioteca_ag_backend.dto.autor.AutorRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.livro.*;
import br.gov.biblioteca_ag_backend.service.LivroService;
import br.gov.biblioteca_ag_backend.validation.RequestValidator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LivroController.class)
@AutoConfigureMockMvc(addFilters = false)
class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LivroService livroService;

    @MockitoBean
    private RequestValidator requestValidator;

    private LivroRespostaDTO criarLivroResposta() {
        return new LivroRespostaDTO(
                1L,
                "Literatura",
                "Dom Casmurro",
                "9788535910663",
                "Romance de Machado de Assis",
                1899,
                "capas/livro.webp"
        );
    }

    private LivroResumoDTO criarLivroResumo() {
        return new LivroResumoDTO(
                1L,
                "Literatura",
                "Dom Casmurro",
                "9788535910663",
                "Editora Companhia das Letras",
                "Romance de Machado de Assis",
                1899,
                List.of(new AutorRespostaDTO(2L, "Machado de Assis"))
        );
    }

    private MockMultipartFile criarParteDados(String json) {
        return new MockMultipartFile(
                "dados",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes()
        );
    }

    private MockMultipartFile criarParteCapa() {
        return new MockMultipartFile(
                "capa",
                "capa.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[]{1, 2, 3, 4}
        );
    }

    @Test
    void listarTodos_DeveRetornarStatus200() throws Exception {

        when(livroService.listarTodos())
                .thenReturn(List.of(criarLivroResposta()));

        mockMvc.perform(get("/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$[0].categoria").value("Literatura"))
                .andExpect(jsonPath("$[0].capa").value("capas/livro.webp"));

        verify(livroService).listarTodos();
    }

    @Test
    void listarTodos_DeveRetornarListaVazia() throws Exception {

        when(livroService.listarTodos())
                .thenReturn(List.of());

        mockMvc.perform(get("/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(livroService).listarTodos();
    }

    @Test
    void procurarPorId_DeveRetornarStatus200() throws Exception {

        when(livroService.buscarPorId(1L))
                .thenReturn(criarLivroResumo());

        mockMvc.perform(get("/livros/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.categoria").value("Literatura"))
                .andExpect(jsonPath("$.autores").isArray())
                .andExpect(jsonPath("$.autores.length()").value(1))
                .andExpect(jsonPath("$.autores[0].nome").value("Machado de Assis"));

        verify(livroService).buscarPorId(1L);
    }

    @Test
    void cadastrar_DeveRetornarStatus201() throws Exception {

        when(livroService.cadastrar(
                any(CriarLivroDTO.class),
                any()
        )).thenReturn(criarLivroResposta());

        MockMultipartFile dados = criarParteDados("""
                {
                    "id_categoria": 1,
                    "id_autores": [2],
                    "titulo": "Dom Casmurro",
                    "isbn": "9788535910663",
                    "editora": "Editora Companhia das Letras",
                    "sinopse": "Romance de Machado de Assis",
                    "anoPublicacao": 1899
                }
                """);

        mockMvc.perform(multipart("/livros")
                        .file(dados)
                        .file(criarParteCapa()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.capa").value("capas/livro.webp"));

        verify(requestValidator).validar(
                any(CriarLivroDTO.class),
                any(),
                eq(true),
                eq("capa")
        );

        verify(livroService).cadastrar(
                any(CriarLivroDTO.class),
                any()
        );
    }

    @Test
    void atualizarCompleto_DeveRetornarStatus200() throws Exception {

        when(livroService.atualizarCompleto(
                eq(1L),
                any(CriarLivroDTO.class),
                any()
        )).thenReturn(criarLivroResposta());

        MockMultipartFile dados = criarParteDados("""
                {
                    "id_categoria": 1,
                    "id_autores": [2],
                    "titulo": "Dom Casmurro",
                    "isbn": "9788535910663",
                    "editora": "Editora Companhia das Letras",
                    "sinopse": "Romance de Machado de Assis",
                    "anoPublicacao": 1899
                }
                """);

        mockMvc.perform(multipart("/livros/{id}", 1L)
                        .file(dados)
                        .file(criarParteCapa())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"));

        verify(requestValidator).validar(
                any(CriarLivroDTO.class),
                any(),
                eq(true),
                eq("capa")
        );

        verify(livroService).atualizarCompleto(
                eq(1L),
                any(CriarLivroDTO.class),
                any()
        );
    }

    @Test
    void atualizar_DeveRetornarStatus200() throws Exception {

        when(livroService.atualizar(
                eq(1L),
                any(AtualizarLivroDTO.class),
                any()
        )).thenReturn(criarLivroResposta());

        MockMultipartFile dados = criarParteDados("""
                {
                    "titulo": "Dom Casmurro - Edição Especial"
                }
                """);

        mockMvc.perform(multipart("/livros/{id}", 1L)
                        .file(dados)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.capa").value("capas/livro.webp"));

        verify(requestValidator).validar(
                any(AtualizarLivroDTO.class)
        );

        verify(livroService).atualizar(
                eq(1L),
                any(AtualizarLivroDTO.class),
                any()
        );
    }

    @Test
    void excluir_DeveRetornarStatus204() throws Exception {

        mockMvc.perform(delete("/livros/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(livroService).deletar(1L);
    }
}
