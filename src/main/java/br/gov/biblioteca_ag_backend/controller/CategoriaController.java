
package br.gov.biblioteca_ag_backend.controller;

import br.gov.biblioteca_ag_backend.dto.categoria.CategoriaRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.categoria.CategoriaResumoDTO;
import br.gov.biblioteca_ag_backend.dto.categoria.CriarCategoriaDTO;
import br.gov.biblioteca_ag_backend.service.CategoriaService;
import br.gov.biblioteca_ag_backend.validation.RequestValidator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final RequestValidator requestValidator;

    public CategoriaController(
            CategoriaService categoriaService,
            RequestValidator requestValidator) {
        this.categoriaService = categoriaService;
        this.requestValidator = requestValidator;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResumoDTO> buscarPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaRespostaDTO>> listarTodos() {
        return ResponseEntity.ok(categoriaService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<CategoriaRespostaDTO> cadastrar(
            @RequestBody CriarCategoriaDTO dto) {

        requestValidator.validar(dto);

        CategoriaRespostaDTO response = categoriaService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaRespostaDTO> atualizarCompleto(
            @PathVariable Long id,
            @RequestBody CriarCategoriaDTO dto) {

        requestValidator.validar(dto);

        CategoriaRespostaDTO response =
                categoriaService.atualizar(id, dto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        categoriaService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
