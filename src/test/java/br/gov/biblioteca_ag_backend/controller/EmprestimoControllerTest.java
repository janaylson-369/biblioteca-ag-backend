
package br.gov.biblioteca_ag_backend.controller;

import br.gov.biblioteca_ag_backend.dto.emprestimo.*;
import br.gov.biblioteca_ag_backend.dto.exemplar.ExemplarRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.usuario.UsuarioRespostaDTO;
import br.gov.biblioteca_ag_backend.enums.StatusEmprestimo;
import br.gov.biblioteca_ag_backend.enums.StatusExemplar;
import br.gov.biblioteca_ag_backend.service.EmprestimoService;
import br.gov.biblioteca_ag_backend.validation.RequestValidator;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmprestimoController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmprestimoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmprestimoService emprestimoService;

    @MockitoBean
    private RequestValidator requestValidator;

    private StatusEmprestimo statusEmprestimo() {
        return StatusEmprestimo.values()[0];
    }

    private EmprestimoRespostaDTO criarEmprestimoResposta() {
        return new EmprestimoRespostaDTO(
                1L,
                10L,
                "João Silva",
                20L,
                "Clean Code",
                LocalDateTime.of(2026, 11, 14, 14, 0),
                statusEmprestimo()
        );
    }

    private EmprestimoCompletoDTO criarEmprestimoCompleto() {
        UsuarioRespostaDTO usuario = new UsuarioRespostaDTO(
                10L,
                "João Silva",
                "83999999999",
                "joao@email.com"
        );

        ExemplarRespostaDTO exemplar = new ExemplarRespostaDTO(
                20L,
                30L,
                "Clean Code",
                "Estante A",
                "TOMB-001",
                StatusExemplar.values()[0]
        );

        return new EmprestimoCompletoDTO(
                1L,
                usuario,
                exemplar,
                null,
                LocalDateTime.of(2026, 11, 7, 14, 0),
                LocalDateTime.of(2026, 11, 14, 14, 0),
                null,
                statusEmprestimo()
        );
    }

    @Test
    void deveListarTodosOsEmprestimos() throws Exception {
        when(emprestimoService.listarTodos())
                .thenReturn(List.of(criarEmprestimoResposta()));

        mockMvc.perform(get("/emprestimos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].id_usuario").value(10))
                .andExpect(jsonPath("$[0].nome_usuario").value("João Silva"))
                .andExpect(jsonPath("$[0].id_exemplar").value(20))
                .andExpect(jsonPath("$[0].titulo").value("Clean Code"))
                .andExpect(jsonPath("$[0].statusEmprestimo")
                        .value(statusEmprestimo().name()));
    }

    @Test
    void deveRetornarListaVaziaDeEmprestimos() throws Exception {
        when(emprestimoService.listarTodos())
                .thenReturn(List.of());

        mockMvc.perform(get("/emprestimos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deveBuscarEmprestimoPorId() throws Exception {
        when(emprestimoService.buscarPorId(1L))
                .thenReturn(criarEmprestimoCompleto());

        mockMvc.perform(get("/emprestimos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuario.id").value(10))
                .andExpect(jsonPath("$.usuario.nome").value("João Silva"))
                .andExpect(jsonPath("$.exemplar.id").value(20))
                .andExpect(jsonPath("$.exemplar.titulo").value("Clean Code"))
                .andExpect(jsonPath("$.id_reserva").doesNotExist())
                .andExpect(jsonPath("$.dataDevolucaoReal").doesNotExist())
                .andExpect(jsonPath("$.statusEmprestimo")
                        .value(statusEmprestimo().name()));
    }

    @Test
    void deveCadastrarEmprestimoSemReserva() throws Exception {
        when(emprestimoService.cadastrarSemReserva(
                any(CriarEmprestimoDTO.class)))
                .thenReturn(criarEmprestimoCompleto());

        mockMvc.perform(post("/emprestimos/cadastrarsemreserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id_usuario": 10,
                                    "id_exemplar": 20,
                                    "diasEmprestimo": 7
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuario.id").value(10))
                .andExpect(jsonPath("$.exemplar.id").value(20));

        verify(requestValidator).validar(any(CriarEmprestimoDTO.class));
        verify(emprestimoService)
                .cadastrarSemReserva(any(CriarEmprestimoDTO.class));
    }

    @Test
    void deveCadastrarEmprestimoComReserva() throws Exception {
        when(emprestimoService.cadastrarComReserva(
                any(CriarEmprestimoComReservaDTO.class)))
                .thenReturn(criarEmprestimoCompleto());

        mockMvc.perform(post("/emprestimos/cadastrarcomreserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id_reserva": 5,
                                    "id_exemplar": 20,
                                    "diasEmprestimo": 7
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuario.id").value(10))
                .andExpect(jsonPath("$.exemplar.id").value(20));

        verify(requestValidator)
                .validar(any(CriarEmprestimoComReservaDTO.class));

        verify(emprestimoService)
                .cadastrarComReserva(any(CriarEmprestimoComReservaDTO.class));
    }
}
