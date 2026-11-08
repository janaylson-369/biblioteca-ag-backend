
package br.gov.biblioteca_ag_backend.controller;

import br.gov.biblioteca_ag_backend.dto.reserva.CriarReservaDTO;
import br.gov.biblioteca_ag_backend.dto.reserva.ReservaCompletoDTO;
import br.gov.biblioteca_ag_backend.dto.reserva.ReservaRespostaDTO;
import br.gov.biblioteca_ag_backend.enums.StatusReserva;
import br.gov.biblioteca_ag_backend.service.ReservaService;
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

@WebMvcTest(ReservaController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservaService reservaService;

    @MockitoBean
    private RequestValidator requestValidator;

    private ReservaRespostaDTO criarReservaResposta() {
        return new ReservaRespostaDTO(
                1L,
                10L,
                "João Silva",
                20L,
                "Clean Code",
                LocalDateTime.of(2026, 11, 7, 14, 0),
                StatusReserva.ATIVA
        );
    }

    private ReservaCompletoDTO criarReservaCompleta() {
        return new ReservaCompletoDTO(
                1L,
                10L,
                "João Silva",
                20L,
                "Clean Code",
                LocalDateTime.of(2026, 11, 7, 14, 0),
                StatusReserva.ATIVA,
                null,
                null
        );
    }

    @Test
    void deveListarTodasAsReservas() throws Exception {
        when(reservaService.listarTodos())
                .thenReturn(List.of(criarReservaResposta()));

        mockMvc.perform(get("/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome_usuario").value("João Silva"))
                .andExpect(jsonPath("$[0].titulo").value("Clean Code"))
                .andExpect(jsonPath("$[0].statusReserva").value("ATIVA"));
    }

    @Test
    void deveRetornarListaVaziaDeReservas() throws Exception {
        when(reservaService.listarTodos())
                .thenReturn(List.of());

        mockMvc.perform(get("/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deveBuscarReservaPorId() throws Exception {
        when(reservaService.buscarPorId(1L))
                .thenReturn(criarReservaCompleta());

        mockMvc.perform(get("/reservas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.id_usuario").value(10))
                .andExpect(jsonPath("$.nome_usuario").value("João Silva"))
                .andExpect(jsonPath("$.id_livro").value(20))
                .andExpect(jsonPath("$.titulo").value("Clean Code"))
                .andExpect(jsonPath("$.statusReserva").value("ATIVA"))
                .andExpect(jsonPath("$.id_emprestimo").doesNotExist())
                .andExpect(jsonPath("$.emprestimo").doesNotExist());
    }

    @Test
    void deveCadastrarReserva() throws Exception {
        when(reservaService.cadastrar(any(CriarReservaDTO.class)))
                .thenReturn(criarReservaResposta());

        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id_livro": 20,
                                    "id_usuario": 10
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.id_usuario").value(10))
                .andExpect(jsonPath("$.id_livro").value(20))
                .andExpect(jsonPath("$.titulo").value("Clean Code"))
                .andExpect(jsonPath("$.statusReserva").value("ATIVA"));

        verify(requestValidator).validar(any(CriarReservaDTO.class));
        verify(reservaService).cadastrar(any(CriarReservaDTO.class));
    }

    @Test
    void deveCancelarReserva() throws Exception {
        when(reservaService.cancelar(1L))
                .thenReturn(new ReservaRespostaDTO(
                        1L,
                        10L,
                        "João Silva",
                        20L,
                        "Clean Code",
                        LocalDateTime.of(2026, 11, 7, 14, 0),
                        StatusReserva.CANCELADA
                ));

        mockMvc.perform(post("/reservas/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statusReserva").value("CANCELADA"));

        verify(reservaService).cancelar(1L);
    }
}
