package br.gov.biblioteca_ag_backend.controller;

import br.gov.biblioteca_ag_backend.dto.livro.AtualizarLivroDTO;
import br.gov.biblioteca_ag_backend.dto.livro.CriarLivroDTO;
import br.gov.biblioteca_ag_backend.dto.livro.LivroRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.livro.LivroResumoDTO;
import br.gov.biblioteca_ag_backend.service.LivroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/livros")
public class LivroController {

    public final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivroResumoDTO> procurarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(livroService.buscarPorId(id));
    }

    @GetMapping()
    public ResponseEntity<List<LivroRespostaDTO>> listarTodos() {
        return ResponseEntity.ok(livroService.listarTodos());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LivroRespostaDTO> cadastrar(@RequestPart("capa") MultipartFile capa, @Valid @RequestPart("dados") CriarLivroDTO dto) {
        LivroRespostaDTO response = livroService.cadastrar(dto, capa);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LivroRespostaDTO> atualizar(@PathVariable Long id, @Valid @RequestPart("dados") CriarLivroDTO dto, @RequestPart("capa") MultipartFile capa) {
        LivroRespostaDTO response = livroService.atualizarCompleto(id, dto, capa);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LivroRespostaDTO> atualizar(@PathVariable Long id, @RequestPart("dados") AtualizarLivroDTO dto, @RequestPart(value = "capa", required = false) MultipartFile capa) {

        LivroRespostaDTO resposta = livroService.atualizar(id, dto, capa);

        return ResponseEntity.ok(resposta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        livroService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
