package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.categoria.CategoriaRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.categoria.CategoriaResumoDTO;
import br.gov.biblioteca_ag_backend.dto.categoria.CriarCategoriaDTO;
import br.gov.biblioteca_ag_backend.model.Categoria;
import br.gov.biblioteca_ag_backend.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public CategoriaRespostaDTO cadastrar(CriarCategoriaDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNome(dto.nome());

        Categoria salvo = categoriaRepository.save(categoria);

        return CategoriaRespostaDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public CategoriaResumoDTO buscarPorId(Long id) {
        Categoria buscado = categoriaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));

        return CategoriaResumoDTO.fromEntity(buscado);
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
        Optional<Categoria> c = categoriaRepository.findById(id);
        if(c.isEmpty()) {
            throw new EntityNotFoundException(String.format("Categoria de id %d inexistente.",id));
        }
        Categoria categoria = c.get();
        categoria.setNome(dto.nome());

        return CategoriaRespostaDTO.fromEntity(categoriaRepository.save(categoria));
    }

    public void excluir(Long id) {
        if(!categoriaRepository.existsById(id)) {
            throw new EntityNotFoundException("Categoria não encotrada");
        }

        categoriaRepository.deleteById(id);
    }


}
