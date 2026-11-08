package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.categoria.CategoriaRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.categoria.CategoriaResumoDTO;
import br.gov.biblioteca_ag_backend.dto.categoria.CriarCategoriaDTO;
import br.gov.biblioteca_ag_backend.exceptions.ResourceNotFoundException;
import br.gov.biblioteca_ag_backend.model.Categoria;
import br.gov.biblioteca_ag_backend.model.Livro;
import br.gov.biblioteca_ag_backend.repository.CategoriaRepository;
import br.gov.biblioteca_ag_backend.repository.LivroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final LivroRepository livroRepository;

    public CategoriaService(
            CategoriaRepository categoriaRepository,
            LivroRepository livroRepository
    ) {
        this.categoriaRepository = categoriaRepository;
        this.livroRepository = livroRepository;
    }

    @Transactional
    public CategoriaRespostaDTO cadastrar(CriarCategoriaDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNome(dto.nome());

        Categoria salva = categoriaRepository.save(categoria);

        return CategoriaRespostaDTO.fromEntity(salva);
    }

    @Transactional(readOnly = true)
    public CategoriaResumoDTO buscarPorId(Long id) {
        Categoria categoria = obterCategoria(id);

        return CategoriaResumoDTO.fromEntity(categoria);
    }

    @Transactional(readOnly = true)
    public List<CategoriaRespostaDTO> listarTodos() {
        return categoriaRepository.findAll()
                .stream()
                .map(CategoriaRespostaDTO::fromEntity)
                .toList();
    }

    @Transactional
    public CategoriaRespostaDTO atualizar(Long id, CriarCategoriaDTO dto) {
        Categoria categoria = obterCategoria(id);

        categoria.setNome(dto.nome());

        return CategoriaRespostaDTO.fromEntity(categoria);
    }

    @Transactional
    public void excluir(Long id) {
        Categoria categoria = obterCategoria(id);

        List<Livro> livros = livroRepository.findByCategoria_Id(id);

        for (Livro livro : livros) {
            livro.setCategoria(null);
        }

        categoriaRepository.delete(categoria);
    }

    // =========================

    private Categoria obterCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "id categoria",
                        String.format("Categoria de id %d inexistente.", id)
                ));
    }
}