
package br.gov.biblioteca_ag_backend.controller;

import br.gov.biblioteca_ag_backend.dto.dadosLeitor.*;
import br.gov.biblioteca_ag_backend.service.DadosLeitorService;
import br.gov.biblioteca_ag_backend.validation.RequestValidator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DadosLeitorController.class)
@AutoConfigureMockMvc(addFilters = false)
class DadosLeitorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DadosLeitorService dadosLeitorService;

    @MockitoBean
    private RequestValidator requestValidator;

    private DadosLeitorCompletoDTO criarDadosLeitorCompleto() {

        DadosLeitorResumoDTO resumo = new DadosLeitorResumoDTO(
                LocalDate.of(2000, 5, 15),
                "Rua das Flores",
                "100",
                "Apartamento 2",
                "Centro"
        );

        return new DadosLeitorCompletoDTO(
                1L,
                "João Silva",
                "83999999999",
                "joao@email.com",
                resumo
        );
    }

    @Test
    void buscarPorId_DeveRetornarStatus200() throws Exception {

        when(dadosLeitorService.buscarPorUsuarioId(1L))
                .thenReturn(criarDadosLeitorCompleto());

        mockMvc.perform(get("/dadosleitor/{id_usuario}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_usuario").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.telefone").value("83999999999"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.dadosLeitor.dataNascimento").value("2000-05-15"))
                .andExpect(jsonPath("$.dadosLeitor.logradouro").value("Rua das Flores"))
                .andExpect(jsonPath("$.dadosLeitor.numero").value("100"))
                .andExpect(jsonPath("$.dadosLeitor.complemento").value("Apartamento 2"))
                .andExpect(jsonPath("$.dadosLeitor.bairro").value("Centro"));

        verify(dadosLeitorService).buscarPorUsuarioId(1L);
    }

    @Test
    void cadastrar_DeveRetornarStatus201() throws Exception {

        when(dadosLeitorService.cadastrarDados(
                eq(1L),
                any(CriarDadosLeitorDTO.class)
        )).thenReturn(criarDadosLeitorCompleto());

        mockMvc.perform(post("/dadosleitor/{id_usuario}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "dataNascimento": "2000-05-15",
                                    "logradouro": "Rua das Flores",
                                    "numero": "100",
                                    "complemento": "Apartamento 2",
                                    "bairro": "Centro"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id_usuario").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.dadosLeitor.dataNascimento").value("2000-05-15"));

        verify(requestValidator).validar(any(CriarDadosLeitorDTO.class));

        verify(dadosLeitorService).cadastrarDados(
                eq(1L),
                any(CriarDadosLeitorDTO.class)
        );
    }

    @Test
    void atualizar_DeveRetornarStatus200() throws Exception {

        when(dadosLeitorService.atualizar(
                eq(1L),
                any(AtualizarDadosLeitorDTO.class)
        )).thenReturn(criarDadosLeitorCompleto());

        mockMvc.perform(patch("/dadosleitor/{id_usuario}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "logradouro": "Avenida Central",
                                    "numero": "200"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_usuario").value(1))
                .andExpect(jsonPath("$.dadosLeitor.logradouro").value("Rua das Flores"));

        verify(requestValidator).validar(any(AtualizarDadosLeitorDTO.class));

        verify(dadosLeitorService).atualizar(
                eq(1L),
                any(AtualizarDadosLeitorDTO.class)
        );
    }
}
