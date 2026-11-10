package br.gov.biblioteca_ag_backend.controller;

import br.gov.biblioteca_ag_backend.dto.reserva.CriarReservaDTO;
import br.gov.biblioteca_ag_backend.dto.reserva.ReservaCompletoDTO;
import br.gov.biblioteca_ag_backend.dto.reserva.ReservaRespostaDTO;
import br.gov.biblioteca_ag_backend.service.ReservaService;
import br.gov.biblioteca_ag_backend.validation.RequestValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {
    private final ReservaService reservaService;
    private final RequestValidator requestValidator;

    public ReservaController(ReservaService reservaService, RequestValidator requestValidator) {
        this.reservaService = reservaService;
        this.requestValidator = requestValidator;
    }

    @GetMapping
    public ResponseEntity<List<ReservaRespostaDTO>> listarTodos() {
        return ResponseEntity.ok(reservaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaCompletoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ReservaRespostaDTO> cadastrar(@RequestBody CriarReservaDTO dto) {
        requestValidator.validar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.cadastrar(dto));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<ReservaRespostaDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.cancelar(id));
    }
}
