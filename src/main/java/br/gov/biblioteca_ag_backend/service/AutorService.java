package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.autor.AutorRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.autor.AutorResumoDTO;
import br.gov.biblioteca_ag_backend.dto.autor.CriarAutorDTO;
import br.gov.biblioteca_ag_backend.exceptions.EmptyRequestException;
import br.gov.biblioteca_ag_backend.exceptions.RequestValidationException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceAlreadyExistsException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceNotFoundException;
import br.gov.biblioteca_ag_backend.model.Autor;
import br.gov.biblioteca_ag_backend.model.Livro;
import br.gov.biblioteca_ag_backend.repository.AutorRepository;
import br.gov.biblioteca_ag_backend.repository.LivroRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AutorService {
    private final AutorRepository autorRepository;
    private final LivroRepository livroRepository;

    public AutorService(AutorRepository autorRepository, LivroRepository livroRepository) {
        this.autorRepository = autorRepository;
        this.livroRepository = livroRepository;
    }

    public AutorRespostaDTO cadastrar(CriarAutorDTO dto) {
        Autor autor = new Autor();
        autor.setNome(dto.nome());

        Autor salvo = autorRepository.save(autor);

        return AutorRespostaDTO.fromEntity(salvo);
    }

    public AutorRespostaDTO atualizarCompleto(Long id, CriarAutorDTO dto) {
        Autor autor = autorRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("nao encontrado",String.format("autor de id %d inexistente", id)));

        autor.setNome(dto.nome());

        Autor salvo = autorRepository.save(autor);
        return AutorRespostaDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public AutorResumoDTO buscarPorId(Long id) {
        Autor buscado = autorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Autor não encontrado"));

        return AutorResumoDTO.fromEntity(buscado);
    }

    @Transactional(readOnly = true)
    public List<AutorRespostaDTO> listarTodos() {
        return autorRepository.findAll().stream().map(AutorRespostaDTO::fromEntity).toList();
    }

    @Transactional
    public void excluir(Long id) {
        Autor buscado = autorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("id autor", String.format("autor de id %d nao encontrado", id)));

        Set<Livro> livros = buscado.getLivros();

        if (!livros.isEmpty()) {
            Map<String, String> errors = new HashMap<>();

            List<Long> idLivros = new ArrayList<>();
            livros.forEach(livro -> idLivros.add(livro.getId()));

            errors.put("dependencia de autor", String.format("autor possui %d livros cadastrados", livros.size()));
            errors.put("id livros", idLivros.toString());

            throw new RequestValidationException(errors);
        }


        autorRepository.deleteById(id);
    }

    @Transactional
    public AutorResumoDTO adicionarLivros(Long autorId, List<Long> livrosIds) {

        if (livrosIds == null || livrosIds.isEmpty()) {
            throw new EmptyRequestException("livrosIds", "Lista de IDs de livros não pode ser vazia");
        }

        Autor autor = autorRepository.findById(autorId).orElseThrow(() -> new ResourceNotFoundException("autorId", "Autor não encontrado"));

        List<Livro> livrosEncontrados = livroRepository.findAllById(livrosIds);

        Set<Long> idsEncontrados = livrosEncontrados.stream().map(Livro::getId).collect(Collectors.toSet());

        List<Long> idsNaoEncontrados = livrosIds.stream().filter(id -> !idsEncontrados.contains(id)).distinct().toList();

        if (!idsNaoEncontrados.isEmpty()) {
            throw new ResourceNotFoundException("livrosIds", "Livros não encontrados: " + idsNaoEncontrados);
        }

        Set<Long> idsAssociados = autor.getLivros().stream().map(Livro::getId).collect(Collectors.toSet());

        List<Long> livrosJaAssociados = livrosIds.stream().filter(idsAssociados::contains).distinct().toList();

        if (!livrosJaAssociados.isEmpty()) {
            throw new ResourceAlreadyExistsException("livrosIds", "Livros já associados a este autor: " + livrosJaAssociados);
        }

        autor.getLivros().addAll(livrosEncontrados);

        Autor salvo = autorRepository.save(autor);

        return AutorResumoDTO.fromEntity(salvo);
    }

    @Transactional
    public AutorResumoDTO removerLivros(Long autorId, List<Long> livrosIds) {

        if (livrosIds == null || livrosIds.isEmpty()) {
            throw new EmptyRequestException("livrosIds", "Lista de IDs de livros não pode ser vazia");
        }

        Autor autor = autorRepository.findById(autorId).orElseThrow(() -> new ResourceNotFoundException("autorId", "autor não encontrado"));

        List<Livro> livrosEncontrados = livroRepository.findAllById(livrosIds);

        Set<Long> idsEncontrados = livrosEncontrados.stream().map(Livro::getId).collect(Collectors.toSet());

        List<Long> idsNaoEncontrados = livrosIds.stream().filter(id -> !idsEncontrados.contains(id)).distinct().toList();

        if (!idsNaoEncontrados.isEmpty()) {
            throw new ResourceNotFoundException("livrosIds", "Livros não encontrados: " + idsNaoEncontrados);
        }

        Set<Long> idsAssociados = autor.getLivros().stream().map(Livro::getId).collect(Collectors.toSet());

        List<Long> livrosNaoAssociados = livrosIds.stream().filter(id -> !idsAssociados.contains(id)).distinct().toList();

        if (!livrosNaoAssociados.isEmpty()) {
            throw new ResourceNotFoundException("livrosIds", "Livros não associados a este autor: " + livrosNaoAssociados);
        }

        livrosEncontrados.forEach(autor.getLivros()::remove);

        Autor salvo = autorRepository.save(autor);

        return AutorResumoDTO.fromEntity(salvo);
    }
}
