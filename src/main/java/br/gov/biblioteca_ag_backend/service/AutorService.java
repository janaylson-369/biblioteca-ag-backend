package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.autor.AutorRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.autor.CriarAutorDTO;
import br.gov.biblioteca_ag_backend.model.Autor;
import br.gov.biblioteca_ag_backend.repository.AutorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AutorService {
    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public AutorRespostaDTO cadastrar(CriarAutorDTO dto) {
        Autor autor = new Autor();
        autor.setNome(dto.nome());

        Autor salvo = autorRepository.save(autor);

        return AutorRespostaDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public AutorRespostaDTO buscarPorId(Long id) {
        Autor buscado = autorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Autor não encontrado"));

        return AutorRespostaDTO.fromEntity(buscado);
    }

    @Transactional(readOnly = true)
    public List<AutorRespostaDTO> listarTodos() {
        return autorRepository.findAll()
                .stream()
                .map(AutorRespostaDTO::fromEntity)
                .toList();
    }

    public void excluir(Long id) {
        if(!autorRepository.existsById(id)){
            throw new EntityNotFoundException("Autor não encontrado");
        }

        autorRepository.deleteById(id);
    }
}
