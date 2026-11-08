package br.gov.biblioteca_ag_backend.controller;

import br.gov.biblioteca_ag_backend.dto.autor.AutorRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.autor.AutorResumoDTO;
import br.gov.biblioteca_ag_backend.dto.autor.CriarAutorDTO;
import br.gov.biblioteca_ag_backend.service.AutorService;
import br.gov.biblioteca_ag_backend.validation.RequestValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/autores")
public class AutorController {

    private final AutorService autorService;
    private final RequestValidator requestValidator;

    public AutorController(AutorService autorService, RequestValidator requestValidator) {
        this.autorService = autorService;
        this.requestValidator = requestValidator;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutorResumoDTO> procurarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(autorService.buscarPorId(id));
    }

    @GetMapping()
    public ResponseEntity<List<AutorRespostaDTO>> listarTodos() {
        return ResponseEntity.ok(autorService.listarTodos());
    }

    @PostMapping()
    public ResponseEntity<AutorRespostaDTO> cadastrar(@RequestBody CriarAutorDTO dto) {
        requestValidator.validar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(autorService.cadastrar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutorRespostaDTO> atualizarCompleto(@PathVariable Long id, @RequestBody CriarAutorDTO dto) {
        requestValidator.validar(dto);

        return ResponseEntity.ok(autorService.atualizarCompleto(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        autorService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/livros")
    public ResponseEntity<AutorResumoDTO> adicionarLivros(
            @PathVariable Long id,
            @RequestBody List<Long> id_livros) {

        return ResponseEntity.ok(autorService.adicionarLivros(id, id_livros));
    }

    @DeleteMapping("/{id}/livros")
    public ResponseEntity<AutorResumoDTO> removerLivros(
            @PathVariable Long id,
            @RequestBody List<Long> id_livros) {

        return ResponseEntity.ok(autorService.removerLivros(id, id_livros));
    }
}
