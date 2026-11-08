
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
            Categoria categoria = obterCategoria(dto.id_categoria());
            livro.setCategoria(categoria);
        }

        if (dto.id_autores() != null && !dto.id_autores().isEmpty()) {
            Set<Autor> autores = buscarAutores(dto.id_autores());

            for (Autor autor : autores) {
                livro.getAutores().add(autor);
                autor.getLivros().add(livro);
            }
        }

        String caminhoCapa = capaStorageService.salvar(arquivoCapa);

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
    public LivroRespostaDTO atualizar(
            Long id,
            AtualizarLivroDTO dto,
            MultipartFile novaCapa
    ) {

        Livro livro = obterLivro(id);

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
            livro.setCategoria(obterCategoria(dto.id_categoria()));
        }

        if (dto.id_autores() != null) {
            substituirAutores(livro, dto.id_autores());
        }

        if (novaCapa != null && !novaCapa.isEmpty()) {
            substituirCapa(livro, novaCapa);
        }

        return LivroRespostaDTO.fromEntity(livro);
    }

    @Transactional
    public LivroRespostaDTO atualizarCompleto(
            Long id,
            CriarLivroDTO dto,
            MultipartFile novaCapa
    ) {

        Livro livro = obterLivro(id);

        if (novaCapa == null || novaCapa.isEmpty()) {
            throw new EmptyRequestException(
                    "novaCapa",
                    "Uma nova capa é obrigatória para atualizar o livro."
            );
        }

        livro.setTitulo(dto.titulo());
        livro.setIsbn(dto.isbn());
        livro.setEditora(dto.editora());
        livro.setSinopse(dto.sinopse());
        livro.setAnoPublicacao(dto.anoPublicacao());

        if (dto.id_categoria() != null) {
            livro.setCategoria(obterCategoria(dto.id_categoria()));
        } else {
            livro.setCategoria(null);
        }

        Set<Long> idsAutores = dto.id_autores() == null
                ? Set.of()
                : dto.id_autores();

        substituirAutores(livro, idsAutores);

        substituirCapa(livro, novaCapa);

        return LivroRespostaDTO.fromEntity(livro);
    }

    @Transactional
    public void deletar(Long id) {

        Livro livro = obterLivro(id);

        Map<String, String> dependencias = new HashMap<>();

        if (exemplarRepository.existsByLivro_Id(id)) {
            dependencias.put(
                    "exemplares",
                    "O livro possui exemplares cadastrados."
            );
        }

        if (reservaRepository.existsByLivro_Id(id)) {
            dependencias.put(
                    "reservas",
                    "O livro possui reservas cadastradas."
            );
        }

        if (emprestimoRepository.existsByExemplar_Livro_Id(id)) {
            dependencias.put(
                    "emprestimos",
                    "O livro possui histórico de empréstimos."
            );
        }

        if (!dependencias.isEmpty()) {
            throw new RequestValidationException(dependencias);
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

    private void substituirAutores(Livro livro, Set<Long> idsAutores) {

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

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            log.warn("Não foi possível registrar exclusão após commit: {}", caminho);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        try {
                            capaStorageService.excluir(caminho);
                        } catch (RuntimeException e) {
                            log.error(
                                    "Falha ao excluir capa antiga: {}",
                                    caminho,
                                    e
                            );
                        }
                    }
                }
        );
    }

    private void excluirSeRollback(String caminho) {

        if (caminho == null || caminho.isBlank()) {
            return;
        }

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            log.warn("Não foi possível registrar limpeza de rollback: {}", caminho);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCompletion(int status) {

                        if (status == STATUS_ROLLED_BACK) {
                            try {
                                capaStorageService.excluir(caminho);
                            } catch (RuntimeException e) {
                                log.error(
                                        "Falha ao limpar capa após rollback: {}",
                                        caminho,
                                        e
                                );
                            }
                        }
                    }
                }
        );
    }
}
