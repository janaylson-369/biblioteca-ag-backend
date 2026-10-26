package br.gov.biblioteca_ag_backend.service;

import br.gov.biblioteca_ag_backend.dto.livro.AtualizarLivroDTO;
import br.gov.biblioteca_ag_backend.dto.livro.CriarLivroDTO;
import br.gov.biblioteca_ag_backend.dto.livro.LivroRespostaDTO;
import br.gov.biblioteca_ag_backend.dto.livro.LivroResumoDTO;
import br.gov.biblioteca_ag_backend.model.Autor;
import br.gov.biblioteca_ag_backend.model.Categoria;
import br.gov.biblioteca_ag_backend.model.Livro;
import br.gov.biblioteca_ag_backend.repository.AutorRepository;
import br.gov.biblioteca_ag_backend.repository.CategoriaRepository;
import br.gov.biblioteca_ag_backend.repository.LivroRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class LivroService {
    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;
    private final CategoriaRepository categoriaRepository;
    private final CapaStorageService capaStorageService;

    public LivroService(LivroRepository livroRepository, AutorRepository autorRepository, CategoriaRepository categoriaRepository, CapaStorageService capaStorageService) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
        this.categoriaRepository = categoriaRepository;
        this.capaStorageService = capaStorageService;
    }

    @Transactional
    public LivroRespostaDTO cadastrar(
            CriarLivroDTO dto,
            MultipartFile arquivoCapa) {

        Livro livro = new Livro();

        livro.setTitulo(dto.titulo());
        livro.setIsbn(dto.isbn());
        livro.setEditora(dto.editora());
        livro.setSinopse(dto.sinopse());
        livro.setAnoPublicacao(dto.anoPublicacao());


        if (dto.id_categoria() != null) {
            Categoria categoria = categoriaRepository
                    .findById(dto.id_categoria())
                    .orElseThrow(() ->
                            new EntityNotFoundException(
                                    "Categoria não encontrada: "
                                            + dto.id_categoria()));

            livro.setCategoria(categoria);
        }

        Set<Long> idsAutores = dto.id_autores();

        if (idsAutores != null && !idsAutores.isEmpty()) {

            Set<Autor> autores = new HashSet<>(
                    autorRepository.findAllById(idsAutores));

            if (autores.size() != idsAutores.size()) {
                throw new EntityNotFoundException(
                        "Um ou mais autores não foram encontrados.");
            }

            for (Autor autor : autores) {
                livro.getAutores().add(autor);

                // Atualiza o lado proprietario
                autor.getLivros().add(livro);
            }
        }

        //Valida, converte e salva a capa como WebP.
        String caminhoCapa =
                capaStorageService.salvar(arquivoCapa);

        livro.setCapa(caminhoCapa);

        try {
            Livro salvo = livroRepository.save(livro);

            return LivroRespostaDTO.fromEntity(salvo);

        } catch (RuntimeException e) {

            // Se o cadastro falhar, tenta remover o arquivo que foi salvo fora da transação do banco.
            try {
                capaStorageService.excluir(caminhoCapa);
            } catch (RuntimeException erroExclusao) {
                e.addSuppressed(erroExclusao);
            }

            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<LivroRespostaDTO> listarTodos() {
        return livroRepository.findAll().stream().map(LivroRespostaDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public LivroResumoDTO buscarPorId(Long id) {
        Livro livro = livroRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Livro com id %d não encontrado", id)));

        return LivroResumoDTO.fromEntity(livro);
    }

    @Transactional
    public LivroRespostaDTO atualizar(
            Long id,
            AtualizarLivroDTO dto,
            MultipartFile novaCapa) {

        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Livro não encontrado: " + id));

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
            Categoria categoria = categoriaRepository
                    .findById(dto.id_categoria())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Categoria não encontrada: "
                                    + dto.id_categoria()));

            livro.setCategoria(categoria);
        }

        if (dto.id_autores() != null) {
            Set<Autor> novosAutores = new HashSet<>(
                    autorRepository.findAllById(dto.id_autores()));

            if (novosAutores.size() != dto.id_autores().size()) {
                throw new EntityNotFoundException(
                        "Um ou mais autores não foram encontrados.");
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

        // Substitui a capa somente se uma nova foi enviada.
        if (novaCapa != null && !novaCapa.isEmpty()) {
            String capaAntiga = livro.getCapa();
            String capaNova = capaStorageService.salvar(novaCapa);

            excluirSeRollback(capaNova);

            livro.setCapa(capaNova);

            excluirAposCommit(capaAntiga);
        }

        return LivroRespostaDTO.fromEntity(
                livroRepository.save(livro));
    }

    @Transactional
    public LivroRespostaDTO atualizarCompleto(
            Long id,
            CriarLivroDTO dto,
            MultipartFile novaCapa) {

        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Livro não encontrado: " + id));

        livro.setTitulo(dto.titulo());
        livro.setIsbn(dto.isbn());
        livro.setEditora(dto.editora());
        livro.setSinopse(dto.sinopse());
        livro.setAnoPublicacao(dto.anoPublicacao());


        if (dto.id_categoria() != null) {
            Categoria categoria = categoriaRepository
                    .findById(dto.id_categoria())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Categoria não encontrada: "
                                    + dto.id_categoria()));

            livro.setCategoria(categoria);
        } else {
            livro.setCategoria(null);
        }

        Set<Long> idsAutores = dto.id_autores() == null
                ? Set.of()
                : dto.id_autores();

        Set<Autor> novosAutores = new HashSet<>(
                autorRepository.findAllById(idsAutores));

        if (novosAutores.size() != idsAutores.size()) {
            throw new EntityNotFoundException(
                    "Um ou mais autores não foram encontrados.");
        }

        // Remove os vínculos antigos pelo lado proprietário.
        for (Autor autor : new HashSet<>(livro.getAutores())) {
            autor.getLivros().remove(livro);
        }
        livro.getAutores().clear();

        // Cria os novos vínculos.
        for (Autor autor : novosAutores) {
            livro.getAutores().add(autor);
            autor.getLivros().add(livro);
        }

        // Guarda a referência da capa anterior.
        String capaAntiga = livro.getCapa();

        // Salva a nova imagem WebP.
        String capaNova = capaStorageService.salvar(novaCapa);

        // Se o banco sofrer rollback, tenta remover a imagem nova.
        excluirSeRollback(capaNova);

        livro.setCapa(capaNova);

        Livro salvo = livroRepository.save(livro);

        excluirAposCommit(capaAntiga);

        return LivroRespostaDTO.fromEntity(salvo);
    }

    @Transactional
    public void deletar(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        for (Autor autor : new HashSet<>(livro.getAutores())) {
            autor.getLivros().remove(livro);
        }

        livro.getAutores().clear();

        String capa = livro.getCapa();

        livroRepository.delete(livro);

        excluirAposCommit(capa);
    }

    private void excluirAposCommit(String caminho) {
        if (caminho == null || caminho.isBlank()) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            capaStorageService.excluir(caminho);
                        } catch (RuntimeException e) {
                            // Registrar em log para permitir limpeza posterior.
                            System.err.println(
                                    "Falha ao excluir capa antiga: "
                                            + e.getMessage());
                        }
                    }
                });
    }

    private void excluirSeRollback(String caminho) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_ROLLED_BACK) {
                            try {
                                capaStorageService.excluir(caminho);
                            } catch (RuntimeException e) {
                                System.err.println(
                                        "Falha ao limpar capa nova: "
                                                + e.getMessage());
                            }
                        }
                    }
                });
    }
}
