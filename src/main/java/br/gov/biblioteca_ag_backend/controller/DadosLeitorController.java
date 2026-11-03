package br.gov.biblioteca_ag_backend.controller;

import br.gov.biblioteca_ag_backend.dto.dadosLeitor.AtualizarDadosLeitorDTO;
import br.gov.biblioteca_ag_backend.dto.dadosLeitor.CriarDadosLeitorDTO;
import br.gov.biblioteca_ag_backend.dto.dadosLeitor.DadosLeitorCompletoDTO;
import br.gov.biblioteca_ag_backend.service.DadosLeitorService;
import br.gov.biblioteca_ag_backend.validation.RequestValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dadosleitor")
public class DadosLeitorController {
    private final DadosLeitorService dadosLeitorService;
    private final RequestValidator requestValidator;

    public DadosLeitorController(DadosLeitorService dadosLeitorService, RequestValidator requestValidator) {
        this.dadosLeitorService = dadosLeitorService;
        this.requestValidator = requestValidator;
    }

    @GetMapping("/{id_usuario}")
    public ResponseEntity<DadosLeitorCompletoDTO> buscarPorId(@PathVariable Long id_usuario) {
        return ResponseEntity.ok(dadosLeitorService.buscarPorUsuarioId(id_usuario));
    }

    @PostMapping("/{id_usuario}")
    public ResponseEntity<DadosLeitorCompletoDTO> cadastrar(@PathVariable Long id_usuario, @RequestBody CriarDadosLeitorDTO dto) {
        requestValidator.validar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(dadosLeitorService.cadastrarDados(id_usuario,dto));
    }

    @PatchMapping("/{id_usuario}")
    public ResponseEntity<DadosLeitorCompletoDTO> atualizar(@PathVariable Long id_usuario, @RequestBody AtualizarDadosLeitorDTO dto) {
        requestValidator.validar(dto);

        return ResponseEntity.ok(dadosLeitorService.atualizar(id_usuario, dto));
    }
}
