
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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AutorService {

    private final AutorRepository autorRepository;
    private final LivroRepository livroRepository;

    public AutorService(
            AutorRepository autorRepository,
            LivroRepository livroRepository
    ) {
        this.autorRepository = autorRepository;
        this.livroRepository = livroRepository;
    }

    @Transactional
    public AutorRespostaDTO cadastrar(CriarAutorDTO dto) {
        Autor autor = new Autor();
        autor.setNome(dto.nome());

        Autor salvo = autorRepository.save(autor);

        return AutorRespostaDTO.fromEntity(salvo);
    }

    @Transactional
    public AutorRespostaDTO atualizarCompleto(Long id, CriarAutorDTO dto) {
        Autor autor = obterAutor(id);

        autor.setNome(dto.nome());

        return AutorRespostaDTO.fromEntity(autor);
    }

    @Transactional(readOnly = true)
    public AutorResumoDTO buscarPorId(Long id) {
        Autor autor = obterAutor(id);

        return AutorResumoDTO.fromEntity(autor);
    }

    @Transactional(readOnly = true)
    public List<AutorRespostaDTO> listarTodos() {
        return autorRepository.findAll()
                .stream()
                .map(AutorRespostaDTO::fromEntity)
                .toList();
    }

    @Transactional
    public void excluir(Long id) {
        Autor autor = obterAutor(id);

        Set<Livro> livros = autor.getLivros();

        if (!livros.isEmpty()) {
            Map<String, String> errors = new HashMap<>();

            List<Long> idsLivros = livros.stream()
                    .map(Livro::getId)
                    .toList();

            errors.put(
                    "dependencia de autor",
                    String.format(
                            "Autor possui %d livros cadastrados",
                            livros.size()
                    )
            );

            errors.put("id livros", idsLivros.toString());

            throw new RequestValidationException(errors);
        }

        autorRepository.delete(autor);
    }

    @Transactional
    public AutorResumoDTO adicionarLivros(Long autorId, List<Long> livrosIds) {

        validarListaIds(livrosIds);

        Autor autor = obterAutor(autorId);

        List<Livro> livrosEncontrados = livroRepository.findAllById(livrosIds);

        validarLivrosEncontrados(livrosIds, livrosEncontrados);

        Set<Long> idsAssociados = autor.getLivros()
                .stream()
                .map(Livro::getId)
                .collect(Collectors.toSet());

        List<Long> livrosJaAssociados = livrosIds.stream()
                .filter(idsAssociados::contains)
                .distinct()
                .toList();

        if (!livrosJaAssociados.isEmpty()) {
            throw new ResourceAlreadyExistsException(
                    "livrosIds",
                    "Livros já associados a este autor: " + livrosJaAssociados
            );
        }

        for (Livro livro : livrosEncontrados) {
            autor.getLivros().add(livro);
            livro.getAutores().add(autor);
        }

        return AutorResumoDTO.fromEntity(autor);
    }

    @Transactional
    public AutorResumoDTO removerLivros(Long autorId, List<Long> livrosIds) {

        validarListaIds(livrosIds);

        Autor autor = obterAutor(autorId);

        List<Livro> livrosEncontrados = livroRepository.findAllById(livrosIds);

        validarLivrosEncontrados(livrosIds, livrosEncontrados);

        Set<Long> idsAssociados = autor.getLivros()
                .stream()
                .map(Livro::getId)
                .collect(Collectors.toSet());

        List<Long> livrosNaoAssociados = livrosIds.stream()
                .filter(id -> !idsAssociados.contains(id))
                .distinct()
                .toList();

        if (!livrosNaoAssociados.isEmpty()) {
            throw new ResourceNotFoundException(
                    "livrosIds",
                    "Livros não associados a este autor: " + livrosNaoAssociados
            );
        }

        for (Livro livro : livrosEncontrados) {
            autor.getLivros().remove(livro);
            livro.getAutores().remove(autor);
        }

        return AutorResumoDTO.fromEntity(autor);
    }

    //=================================
    private Autor obterAutor(Long id) {
        return autorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "autorId",
                        String.format("Autor de id %d não encontrado", id)
                ));
    }

    private void validarListaIds(List<Long> livrosIds) {
        if (livrosIds == null || livrosIds.isEmpty()) {
            throw new EmptyRequestException(
                    "livrosIds",
                    "Lista de IDs de livros não pode ser vazia"
            );
        }
    }

    private void validarLivrosEncontrados(
            List<Long> livrosIds,
            List<Livro> livrosEncontrados
    ) {
        Set<Long> idsEncontrados = livrosEncontrados.stream()
                .map(Livro::getId)
                .collect(Collectors.toSet());

        List<Long> idsNaoEncontrados = livrosIds.stream()
                .filter(id -> !idsEncontrados.contains(id))
                .distinct()
                .toList();

        if (!idsNaoEncontrados.isEmpty()) {
            throw new ResourceNotFoundException(
                    "livrosIds",
                    "Livros não encontrados: " + idsNaoEncontrados
            );
        }
    }
}
