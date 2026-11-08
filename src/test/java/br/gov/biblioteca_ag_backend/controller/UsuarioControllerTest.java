
package br.gov.biblioteca_ag_backend.controller;

import br.gov.biblioteca_ag_backend.dto.usuario.*;
import br.gov.biblioteca_ag_backend.service.UsuarioService;
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

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private RequestValidator requestValidator;

    private UsuarioRespostaDTO criarUsuarioResposta() {
        return new UsuarioRespostaDTO(
                1L,
                "João Silva",
                "83999999999",
                "joao@email.com"
        );
    }

    private UsuarioResumoDTO criarUsuarioResumo() {
        return new UsuarioResumoDTO(
                1L,
                "João Silva",
                "83999999999",
                "joao@email.com"
        );
    }

    @Test
    void listarTodos_DeveRetornarStatus200() throws Exception {

        when(usuarioService.listarTodos())
                .thenReturn(List.of(criarUsuarioResposta()));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("João Silva"))
                .andExpect(jsonPath("$[0].telefone").value("83999999999"))
                .andExpect(jsonPath("$[0].email").value("joao@email.com"));

        verify(usuarioService).listarTodos();
    }

    @Test
    void listarTodos_DeveRetornarListaVazia() throws Exception {

        when(usuarioService.listarTodos())
                .thenReturn(List.of());

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(usuarioService).listarTodos();
    }

    @Test
    void buscarPorId_DeveRetornarStatus200() throws Exception {

        when(usuarioService.buscarPorId(1L))
                .thenReturn(criarUsuarioResumo());

        mockMvc.perform(get("/usuarios/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.telefone").value("83999999999"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));

        verify(usuarioService).buscarPorId(1L);
    }

    @Test
    void cadastrar_DeveRetornarStatus201() throws Exception {

        when(usuarioService.cadastrar(any(CriarUsuarioDTO.class)))
                .thenReturn(criarUsuarioResposta());

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "João Silva",
                                    "telefone": "83999999999",
                                    "email": "joao@email.com",
                                    "senha_normal": "senha123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.telefone").value("83999999999"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));

        verify(requestValidator).validar(any(CriarUsuarioDTO.class));
        verify(usuarioService).cadastrar(any(CriarUsuarioDTO.class));
    }

    @Test
    void atualizarCompleto_DeveRetornarStatus200() throws Exception {

        when(usuarioService.atualizarCompleto(
                eq(1L),
                any(AtualizarUsuarioCompletoDTO.class)
        )).thenReturn(criarUsuarioResposta());

        mockMvc.perform(put("/usuarios/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "João Silva",
                                    "telefone": "83999999999",
                                    "email": "joao@email.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));

        verify(requestValidator).validar(
                any(AtualizarUsuarioCompletoDTO.class)
        );

        verify(usuarioService).atualizarCompleto(
                eq(1L),
                any(AtualizarUsuarioCompletoDTO.class)
        );
    }

    @Test
    void atualizar_DeveRetornarStatus200() throws Exception {

        when(usuarioService.atualizar(
                eq(1L),
                any(AtualizarUsuarioDTO.class)
        )).thenReturn(criarUsuarioResposta());

        mockMvc.perform(patch("/usuarios/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "João Silva"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"));

        verify(requestValidator).validar(
                any(AtualizarUsuarioDTO.class)
        );

        verify(usuarioService).atualizar(
                eq(1L),
                any(AtualizarUsuarioDTO.class)
        );
    }

    @Test
    void deletar_DeveRetornarStatus204() throws Exception {

        mockMvc.perform(delete("/usuarios/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(usuarioService).excluir(1L);
    }
}
