package br.gov.biblioteca_ag_backend.controller;

import br.gov.biblioteca_ag_backend.dto.emprestimo.CriarEmprestimoComReservaDTO;
import br.gov.biblioteca_ag_backend.dto.emprestimo.CriarEmprestimoDTO;
import br.gov.biblioteca_ag_backend.dto.emprestimo.EmprestimoCompletoDTO;
import br.gov.biblioteca_ag_backend.dto.emprestimo.EmprestimoRespostaDTO;
import br.gov.biblioteca_ag_backend.service.EmprestimoService;
import br.gov.biblioteca_ag_backend.validation.RequestValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {
    private final EmprestimoService emprestimoService;
    private final RequestValidator requestValidator;

    public EmprestimoController(EmprestimoService emprestimoService, RequestValidator requestValidator) {
        this.emprestimoService = emprestimoService;
        this.requestValidator = requestValidator;
    }

    @GetMapping
    public ResponseEntity<List<EmprestimoRespostaDTO>> listarTodos() {
        return ResponseEntity.ok(emprestimoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoCompletoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(emprestimoService.buscarPorId(id));
    }

    @PostMapping("/cadastrarsemreserva")
    public ResponseEntity<EmprestimoCompletoDTO> cadastrarSemReserva(@RequestBody CriarEmprestimoDTO dto) {
        requestValidator.validar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(emprestimoService.cadastrarSemReserva(dto));
    }

    @PostMapping("/cadastrarcomreserva")
    public ResponseEntity<EmprestimoCompletoDTO> cadastrarComReserva(@RequestBody CriarEmprestimoComReservaDTO dto) {
        requestValidator.validar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(emprestimoService.cadastrarComReserva(dto));
    }

    @PostMapping("/finalizar/{id}")
    public ResponseEntity<EmprestimoCompletoDTO> finalizarEmprestimo(@PathVariable Long id) {
        return ResponseEntity.ok(emprestimoService.devolver(id));
    }

    @PostMapping("/renovar/{id}")
    public ResponseEntity<EmprestimoCompletoDTO> renovarEmprestimo(@PathVariable Long id, @RequestParam long diasAdicionais) {
        return ResponseEntity.ok(emprestimoService.renovar(id,diasAdicionais));
    }
}
