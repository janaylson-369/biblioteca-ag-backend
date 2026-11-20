
package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.livro.AtualizarLivroDTO;
import br.gov.biblioteca_ag_backend.dto.livro.CriarLivroDTO;
import br.gov.biblioteca_ag_backend.dto.livro.LivroRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.livro.LivroResumoDTO;

import br.gov.biblioteca_ag_backend.exceptions.EmptyRequestException;
import br.gov.biblioteca_ag_backend.exceptions.RequestValidationException;
import br.gov.biblioteca_ag_backend.exceptions.ResourceNotFoundException;

import br.gov.biblioteca_ag_backend.model.Autor;
import br.gov.biblioteca_ag_backend.model.Categoria;
import br.gov.biblioteca_ag_backend.model.Livro;

import br.gov.biblioteca_ag_backend.repository.AutorRepository;
import br.gov.biblioteca_ag_backend.repository.CategoriaRepository;
import br.gov.biblioteca_ag_backend.repository.LivroRepository;
import br.gov.biblioteca_ag_backend.repository.ExemplarRepository;
import br.gov.biblioteca_ag_backend.repository.ReservaRepository;
import br.gov.biblioteca_ag_backend.repository.EmprestimoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class LivroService {

    private static final Logger log = LoggerFactory.getLogger(LivroService.class);

    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;
    private final CategoriaRepository categoriaRepository;
    private final ExemplarRepository exemplarRepository;
    private final ReservaRepository reservaRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final CapaStorageService capaStorageService;

    public LivroService(
            LivroRepository livroRepository,
            AutorRepository autorRepository,
            CategoriaRepository categoriaRepository,
            ExemplarRepository exemplarRepository,
            ReservaRepository reservaRepository,
            EmprestimoRepository emprestimoRepository,
            CapaStorageService capaStorageService
    ) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
        this.categoriaRepository = categoriaRepository;
        this.exemplarRepository = exemplarRepository;
        this.reservaRepository = reservaRepository;
        this.emprestimoRepository = emprestimoRepository;
        this.capaStorageService = capaStorageService;
    }

    @Transactional
    public LivroRespostaDTO cadastrar(CriarLivroDTO dto, MultipartFile arquivoCapa) {

        Livro livro = new Livro();

        livro.setTitulo(dto.titulo());
        livro.setIsbn(dto.isbn());
        livro.setEditora(dto.editora());
        livro.setSinopse(dto.sinopse());
        livro.setAnoPublicacao(dto.anoPublicacao());

        if (dto.id_categoria() != null) {
            Categoria categoria = categoriaRepository.findById(dto.id_categoria()).orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada: " + dto.id_categoria()));

            livro.setCategoria(categoria);
        }

        Set<Long> idsAutores = dto.id_autores();

        if (idsAutores != null && !idsAutores.isEmpty()) {

            Set<Autor> autores = new HashSet<>(autorRepository.findAllById(idsAutores));

            if (autores.size() != idsAutores.size()) {
                throw new EntityNotFoundException("Um ou mais autores não foram encontrados.");
            }

            for (Autor autor : autores) {
                livro.getAutores().add(autor);
                autor.getLivros().add(livro);
            }
        }

        //Valida, converte e salva a capa como WebP.
        String caminhoCapa = capaStorageService.salvar(arquivoCapa);

        livro.setCapa(caminhoCapa);

        try {
            Livro salvo = livroRepository.save(livro);

        excluirSeRollback(caminhoCapa);

        livro.setCapa(caminhoCapa);

        Livro salvo = livroRepository.save(livro);

        return LivroRespostaDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public List<LivroRespostaDTO> listarTodos() {
        return livroRepository.findAll()
                .stream()
                .map(LivroRespostaDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public LivroResumoDTO buscarPorId(Long id) {
        Livro livro = obterLivro(id);
        return LivroResumoDTO.fromEntity(livro);
    }

    @Transactional
    public LivroRespostaDTO atualizar(Long id, AtualizarLivroDTO dto, MultipartFile novaCapa) {

        Livro livro = livroRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Livro não encontrado: " + id));

        if (dto.titulo() != null) {
            livro.setTitulo(dto.titulo());
        }

        if (dto.isbn() != null) {
            livro.setIsbn(dto.isbn());
        }

        if (dto.editora() != null) {
            livro.setEditora(dto.editora());
        }

        if (dto.sinopse() != null) {
            livro.setSinopse(dto.sinopse());
        }

        if (dto.anoPublicacao() != null) {
            livro.setAnoPublicacao(dto.anoPublicacao());
        }

        if (dto.id_categoria() != null) {
            Categoria categoria = categoriaRepository.findById(dto.id_categoria()).orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada: " + dto.id_categoria()));

            livro.setCategoria(categoria);
        }

        if (dto.id_autores() != null) {
            Set<Autor> novosAutores = new HashSet<>(autorRepository.findAllById(dto.id_autores()));

            if (novosAutores.size() != dto.id_autores().size()) {
                throw new EntityNotFoundException("Um ou mais autores não foram encontrados.");
            }

            // Autor é o lado proprietário do ManyToMany.
            for (Autor autor : new HashSet<>(livro.getAutores())) {
                autor.getLivros().remove(livro);
            }

            livro.getAutores().clear();

            for (Autor autor : novosAutores) {
                livro.getAutores().add(autor);
                autor.getLivros().add(livro);
            }
        }

        if (novaCapa != null && !novaCapa.isEmpty()) {
            substituirCapa(livro, novaCapa);
        }

        return LivroRespostaDTO.fromEntity(livroRepository.save(livro));
    }

    @Transactional
    public LivroRespostaDTO atualizarCompleto(Long id, CriarLivroDTO dto, MultipartFile novaCapa) {

        Livro livro = livroRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Livro não encontrado: " + id));

        livro.setTitulo(dto.titulo());
        livro.setIsbn(dto.isbn());
        livro.setEditora(dto.editora());
        livro.setSinopse(dto.sinopse());
        livro.setAnoPublicacao(dto.anoPublicacao());

        if (dto.id_categoria() != null) {
            Categoria categoria = categoriaRepository.findById(dto.id_categoria()).orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada: " + dto.id_categoria()));

            livro.setCategoria(categoria);
        } else {
            livro.setCategoria(null);
        }

        Set<Long> idsAutores = dto.id_autores() == null ? Set.of() : dto.id_autores();

        Set<Autor> novosAutores = new HashSet<>(autorRepository.findAllById(idsAutores));

        if (novosAutores.size() != idsAutores.size()) {
            throw new EntityNotFoundException("Um ou mais autores não foram encontrados.");
        }

        for (Autor autor : new HashSet<>(livro.getAutores())) {
            autor.getLivros().remove(livro);
        }

        livro.getAutores().clear();

        String capa = livro.getCapa();

        livroRepository.delete(livro);

        excluirAposCommit(capa);
    }

    //==================================================
    private Livro obterLivro(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "id_livro",
                        "Livro com id " + id + " não encontrado."
                ));
    }

    private Categoria obterCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "id_categoria",
                        "Categoria com id " + id + " não encontrada."
                ));
    }

    private Set<Autor> buscarAutores(Set<Long> idsAutores) {

        List<Autor> encontrados = autorRepository.findAllById(idsAutores);

        if (encontrados.size() != idsAutores.size()) {
            Set<Long> idsEncontrados = encontrados.stream()
                    .map(Autor::getId)
                    .collect(java.util.stream.Collectors.toSet());

            List<Long> idsNaoEncontrados = idsAutores.stream()
                    .filter(id -> !idsEncontrados.contains(id))
                    .toList();

            throw new ResourceNotFoundException(
                    "id_autores",
                    "Autores não encontrados: " + idsNaoEncontrados
            );
        }

        return new HashSet<>(encontrados);
    }

    @Transactional
    public void deletar(Long id) {
        Livro livro = livroRepository.findById(id).orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        Set<Autor> novosAutores = idsAutores.isEmpty()
                ? new HashSet<>()
                : buscarAutores(idsAutores);

        // Remove os relacionamentos anteriores.
        for (Autor autor : new HashSet<>(livro.getAutores())) {
            autor.getLivros().remove(livro);
        }

        livro.getAutores().clear();

        // Cria os novos relacionamentos.
        for (Autor autor : novosAutores) {
            livro.getAutores().add(autor);
            autor.getLivros().add(livro);
        }
    }

    private void substituirCapa(Livro livro, MultipartFile novaCapa) {

        String capaAntiga = livro.getCapa();

        String capaNova = capaStorageService.salvar(novaCapa);

        // Se houver rollback, remove a capa nova.
        excluirSeRollback(capaNova);

        livro.setCapa(capaNova);

        // Se houver commit, remove a capa antiga.
        excluirAposCommit(capaAntiga);
    }

    // ARMAZENAMENTO DE CAPAS E TRANSAÇÕES

    private void excluirAposCommit(String caminho) {

        if (caminho == null || caminho.isBlank()) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    capaStorageService.excluir(caminho);
                } catch (RuntimeException e) {
                    // Registrar em log para permitir limpeza posterior.
                    System.err.println("Falha ao excluir capa antiga: " + e.getMessage());
                }
            }
        });
    }

    private void excluirSeRollback(String caminho) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    try {
                        capaStorageService.excluir(caminho);
                    } catch (RuntimeException e) {
                        System.err.println("Falha ao limpar capa nova: " + e.getMessage());
                    }
                }
            }
        });
    }
}
