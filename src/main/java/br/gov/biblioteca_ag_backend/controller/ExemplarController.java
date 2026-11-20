package br.gov.biblioteca_ag_backend.controller;

import br.gov.biblioteca_ag_backend.dto.exemplar.AtualizarExemplarDTO;
import br.gov.biblioteca_ag_backend.dto.exemplar.CriarExemplarDTO;
import br.gov.biblioteca_ag_backend.dto.exemplar.ExemplarRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.exemplar.ExemplarResumoDTO;
import br.gov.biblioteca_ag_backend.service.ExemplarService;
import br.gov.biblioteca_ag_backend.validation.RequestValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exemplares")
public class ExemplarController {
    private final ExemplarService exemplarService;
    private final RequestValidator requestValidator;

    public ExemplarController(ExemplarService exemplarService, RequestValidator requestValidator) {
        this.exemplarService = exemplarService;
        this.requestValidator = requestValidator;
    }

    @GetMapping
    public ResponseEntity<List<ExemplarRespostaDTO>> listarTodos() {
        return ResponseEntity.ok(exemplarService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExemplarResumoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(exemplarService.buscarPorId(id));
    }

    @GetMapping("/livro/{id}")
    public ResponseEntity<List<ExemplarRespostaDTO>> buscarPorLivroId(@PathVariable Long id) {
        return ResponseEntity.ok(exemplarService.buscarPorLivroId(id));
    }

    @PostMapping
    public ResponseEntity<ExemplarRespostaDTO> cadastrar(@RequestBody CriarExemplarDTO dto) {
        requestValidator.validar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(exemplarService.cadastrar(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ExemplarResumoDTO> atualizar(@PathVariable long id,@RequestBody AtualizarExemplarDTO dto) {
        requestValidator.validar(dto);

        return ResponseEntity.ok(exemplarService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        exemplarService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
