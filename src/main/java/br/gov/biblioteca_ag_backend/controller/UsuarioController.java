package br.gov.biblioteca_ag_backend.controller;

import br.gov.biblioteca_ag_backend.dto.usuario.*;
import br.gov.biblioteca_ag_backend.service.UsuarioService;
import br.gov.biblioteca_ag_backend.validation.RequestValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final RequestValidator requestValidator;

    public UsuarioController(UsuarioService usuarioService, RequestValidator requestValidator) {
        this.usuarioService = usuarioService;
        this.requestValidator = requestValidator;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioRespostaDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResumoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioRespostaDTO> cadastrar(@RequestBody CriarUsuarioDTO dto) {
        requestValidator.validar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.cadastrar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioRespostaDTO> atualizarCompleto(@PathVariable Long id, @RequestBody AtualizarUsuarioCompletoDTO dto) {
        requestValidator.validar(dto);

        return ResponseEntity.ok(usuarioService.atualizarCompleto(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioRespostaDTO> atualizar(@PathVariable Long id, @RequestBody AtualizarUsuarioDTO dto) {
        requestValidator.validar(dto);

        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.excluir(id);

        return ResponseEntity.noContent().build();
    }

}
